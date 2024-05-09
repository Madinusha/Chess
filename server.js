const express = require('express');
const app = express();
const session = require('express-session');
const path = require('path');
const sqlite3 = require('sqlite3').verbose();
const bcrypt = require('bcrypt');

const port = 3004; // Порт, на котором хотим запустить сервер

const dbСlients = new sqlite3.Database('./clients.db');

// Создание глобальной переменной на сервере
let sharedValue = null;

// Установка пути к статическим файлам (html, css, js)
app.use(express.static(path.join(__dirname, 'registration')));

app.use(express.json());

// Настройка сессий
app.use(session({
    secret: 'secret-key', // Замените на свой секретный ключ
    resave: false,
    saveUninitialized: true,
    cookie: { secure: false } // В реальном приложении установите secure: true для HTTPS
}));

//// Middleware для проверки аутентификации пользователя
//function requireAuth(req, res, next) {
//    if (req.session.userId) {
//        // Пользователь аутентифицирован, продолжаем выполнение запроса
//        next();
//    } else {
//        // Пользователь не аутентифицирован, перенаправляем на страницу входа или возвращаем ошибку
//        res.status(401).json({ error: 'Требуется аутентификация.' });
//    }
//}

//// Маршрут для проверки аутентификации пользователя
//app.get('/check-authentication', (req, res) => {
//    if (req.session.userId) {
//        console.log("Пользователь аутентифицирован, отправляем идентификатор пользователя в ответе")
//        res.status(200).json({ userId: req.session.userId });
//    } else {
//        // Пользователь не аутентифицирован, возвращаем ошибку
//        res.status(401).json({ error: 'Требуется аутентификация.' });
//    }
//});

// Middleware для проверки аутентификации пользователя
function requireAuth(req, res, next) {
    console.log(req.session.userId)
    if (req.session.userId || sharedValue) {
        // Пользователь аутентифицирован, добавляем данные пользователя к объекту запроса и продолжаем выполнение запроса

        console.log("Авторизован да да")
        next();
    } else {
        // Пользователь не аутентифицирован, возвращаем ошибку
        console.log("Требуется аутентификация, отправляю код 401")
        res.status(401).json({ error: 'Требуется аутентификация.' });
    }
}

// Маршрут для проверки аутентификации пользователя
app.post('/check-authentication', requireAuth, (req, res) => {
    console.log("Пользователь аутентифицирован, отправляем идентификатор пользователя в ответе")
    res.status(200).json(sharedValue);
});

// Маршрут для выхода из аккаунта
app.post('/logout', (req, res) => {
    if (req.session.userId) {
        req.session.destroy(err => {
            if (err) {
                    return res.status(500).json({ error: 'Ошибка при завершении сессии.' });
            }
            res.clearCookie('sessionId'); // Очистить cookie с идентификатором сессии
            res.status(200).json({ message: 'Выход из аккаунта прошел успешно.' });
        });
    } 
    else {
        res.status(401).json({ error: 'Пользователь не авторизован.' });
    }
});

// Маршрут для отображения основной страницы
//app.get('/', (req, res) => {
//    res.sendFile(path.join(__dirname, 'registration', 'registration.html'));
//});

// Маршрут для отображения страницы регистрации
app.get('/registration', (req, res) => {
  res.sendFile(path.join(__dirname, 'registration', 'registration.html'));
});

// Защищенный маршрут, доступный только аутентифицированным пользователям
app.get('/', requireAuth, (req, res) => {
  res.sendFile(path.join(__dirname, 'registration', 'profileInfo.html'));
});

app.get('/profileInfo', requireAuth, (req, res) => {
    const userId = req.session.userId;

    dbСlients.get('SELECT * FROM users WHERE id = ?', [userId], (err, row) => {
        if (err) {
            return res.status(500).json({ error: 'Ошибка при выполнении запроса к базе данных.' });
        }

        if (!row) {
                return res.status(400).json({ error: 'Неверный id.' });
        }
        res.status(200).json(row);
    });
});

// Обработка POST-запроса на авторизацию
app.post('/authorization', (req, res) => {
    const { email, password } = req.body;

    // Проверка наличия обязательных полей
    if (!email || !password) {
     return res.status(400).json({ error: 'Необходимо заполнить все поля' });
    }

    // Проверка наличия пользователя с таким email и password в базе данных
    dbСlients.get('SELECT * FROM users WHERE email = ?', [email], (err, row) => {
       if (err) {
         return res.status(500).json({ error: 'Ошибка при выполнении запроса к базе данных.' });
       }

       if (!row) {
           return res.status(400).json({ error: 'Неверный email или пароль.' });
       }

       // Сравнение хэша пароля из базы данных с предоставленным паролем
       bcrypt.compare(password, row.password, (compareErr, isMatch) => {
         if (compareErr) {
             return res.status(500).json({ error: 'Ошибка при сравнении паролей.' });
         }

         if (!isMatch) {
             return res.status(400).json({ error: 'Неверный email или пароль.' });
         }

         if (row.isBlocked === 1) {
             return res.status(400).json({ error: 'Ваш аккаунт заблокирован.' });
         }

         // Если пароли совпадают, сохраняем идентификатор пользователя в сессии
         req.session.userId = row.id;
         sharedValue = row;
         return res.status(200).json({ message: 'Аутентификация прошла успешно.' });
     });
    });
});

// Обработка POST-запроса на регистрацию
app.post('/registration', (req, res) => {
  console.log(req.body)
  const { nickname, email, password } = req.body;

  // Проверка наличия обязательных полей
  if (!nickname || !email || !password) {
    return res.status(400).json({ error: 'Необходимо заполнить все поля' });
  }

  // Хэширование пароля
  const hashedPassword = bcrypt.hashSync(password, 10);

  // Проверка наличия пользователя с таким email в базе данных
  dbСlients.get('SELECT * FROM users WHERE email = ?', [email], (err, row) => {
    if (err) {
      console.error(err.message);
      return res.status(500).json({ error: 'Ошибка сервера' });
    }

    console.log("Результат запроса:", row);

    if (row) {
      return res.status(400).json({ error: 'Пользователь с таким email уже существует' });
    }

    // Добавление нового пользователя в базу данных
    // dbClients.run('INSERT INTO users (email, password) VALUES (?, ?)', [email, hashedPassword], (err) => {
    dbСlients.run('INSERT INTO users (nickname, email, password, type, isBlocked) VALUES (?, ?, ?, ?, ?)', [nickname, email, hashedPassword, 'user', 0], (err) => {
      if (err) {
        console.error(err.message);
        return res.status(500).json({ error: 'Ошибка сервера' });
      }

      return res.status(200).json({ message: 'Пользователь успешно зарегистрирован' });
    });
  });
});

// Слушаем порт
app.listen(port, () => {
    console.log(`Сервер запущен на порту ${port}`);
});

