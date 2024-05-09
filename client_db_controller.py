import sqlite3

def create_clients_db():
    '''Создать БД «clients.db» с таблицей users'''
    conn = sqlite3.connect('clients.db')

    cur = conn.cursor()

    cur.execute('''CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY,
                    nickname TEXT,
                    email TEXT,
                    password TEXT,
                    type TEXT,
                    isBlocked BOOLEAN
                    )''')

    conn.commit()

    conn.close()


def set_user(email):
    '''Установить значение поля type на "user" для указанного email'''
    conn = sqlite3.connect('clients.db')
    cursor = conn.cursor()

    try:
        cursor.execute("SELECT * FROM users WHERE email = ?", (email,))
        user = cursor.fetchone()
        if user is None:
            print(f"Пользователь с email {email} не найден.")
            return

        user_type = user[4]
        if user_type == 'user':
            print(f"Пользователь с email {email} уже является обычным пользователем.")
            return

        # Устанавливаем тип пользователя на "user"
        cursor.execute("UPDATE users SET type = 'user' WHERE email = ?", (email,))
        conn.commit()
        print(f"Пользователь с email {email} успешно назначен обычным пользователем.")
    except sqlite3.Error as e:
        print("Произошла ошибка при обновлении записи:", e)
    finally:
        conn.close()


if __name__ == '__main__':
    create_clients_db()