import sqlite3

# Connect to SQLite database (or create if it doesn't exist)
conn = sqlite3.connect('digital_wellbeing.db')

# Create a cursor object to execute SQL commands
cur = conn.cursor()

# Create Digital Wellbeing table
cur.execute('''CREATE TABLE IF NOT EXISTS Digital_Wellbeing (
                    id INTEGER PRIMARY KEY,
                    app_name TEXT,
                    limit_duration INTEGER,
                    warning_threshold INTEGER,
                    current_usage INTEGER,
                    date TEXT,
                    goal_type TEXT,
                    goal_deadline TEXT,
                    goal_status TEXT,
                    streak_type TEXT,
                    start_date TEXT,
                    end_date TEXT,
                    streak_status TEXT
                )''')

# Mock entries
mock_entries = [
    ('App1', 120, 90, 60, '2024-04-10', 'Screen Time Reduction', '2024-04-15', 'Ongoing', 'Productivity', '2024-04-10', '2024-04-10', 'Active'),
    ('App2', 90, 60, 45, '2024-04-10', 'Task Completion', '2024-04-20', 'Ongoing', 'Mindfulness', '2024-04-10', '2024-04-10', 'Active'),
    ('App3', 180, 120, 90, '2024-04-10', 'Screen Time Reduction', '2024-04-18', 'Achieved', 'Productivity', '2024-04-10', '2024-04-17', 'Completed')
]

# Insert mock entries into the Digital Wellbeing table
cur.executemany('''INSERT INTO Digital_Wellbeing (
                        app_name, limit_duration, warning_threshold, current_usage, 
                        date, goal_type, goal_deadline, goal_status, streak_type, 
                        start_date, end_date, streak_status
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)''', mock_entries)

# Commit changes and close connection
conn.commit()
conn.close()

print("Database created successfully with mock entries.")
