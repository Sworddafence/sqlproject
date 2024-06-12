from flask import Flask, request, render_template, redirect, url_for
import subprocess


app = Flask(__name__)

DIR = '/home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts'
USER = 'jlee1442'
PGPORT = '21420'
command = "java -cp /home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../classes:/home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../lib/pg73jdbc3.jar GameRental jlee1442_project_phase_3_DB 21420 jlee1442"

# In-memory storage for user data (replace this with a database in a real application)
users = {'admin': 'admin'}

USERISGLOBAL = ""


def generate_html_table(game_data):
    games = game_data.split('|')
    
    # Split the data into rows of three (name, price, code)
    rows = [games[i:i + 3] for i in range(0, len(games), 3)]
    
    # Start the HTML table
    html = """
    <table border="1" cellpadding="5" cellspacing="0">
        <thead>
            <tr>
                <th>Name</th>
                <th>Price</th>
                <th>Code</th>
            </tr>
        </thead>
        <tbody>
    """
    
    # Add each row to the table
    for row in rows:
        if len(row) == 3:
            html += f"""
            <tr>
                <td>{row[0].strip()}</td>
                <td>{row[1].strip()}</td>
                <td>{row[2].strip()}</td>
            </tr>
            """
    
    # Close the table
    html += """
        </tbody>
    </table>
    """
    
    return html

@app.route('/hi')
def getlogin(user, pwd):
    command = f"java -cp /home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../classes:/home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../lib/pg73jdbc3.jar GameRental jlee1442_project_phase_3_DB 21420 jlee1442 1 {user} {pwd}"
    
    result = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)
    print(result.stdout)
    if "True" in result.stdout:
        return True
    else:
        return False

def createUser(user, pwd, num):
    command = f"java -cp /home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../classes:/home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../lib/pg73jdbc3.jar GameRental jlee1442_project_phase_3_DB 21420 jlee1442 2 {user} {pwd} {num}"
    result = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)
    print(result.stdout)
    if "True" in result.stdout:
        return True
    else:
        return False

def getprofile(user):
    command = f"java -cp /home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../classes:/home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../lib/pg73jdbc3.jar GameRental jlee1442_project_phase_3_DB 21420 jlee1442 3 {user}"
    result = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)
    return result.stdout

def updateprofile(user):
    command = f"java -cp /home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../classes:/home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../lib/pg73jdbc3.jar GameRental jlee1442_project_phase_3_DB 21420 jlee1442 3 {user}"
    result = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)
    return result.stdout

def changepwd(user, pwd):
    command = f"java -cp /home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../classes:/home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../lib/pg73jdbc3.jar GameRental jlee1442_project_phase_3_DB 21420 jlee1442 4 {user} 2 2 {pwd}"
    print(command)
    result = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)
    return result.stdout

def changefav(user, change, isadd):
    command = f"java -cp /home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../classes:/home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../lib/pg73jdbc3.jar GameRental jlee1442_project_phase_3_DB 21420 jlee1442 4 {user} 1 {change} '{isadd}'"
    result = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)
    print(result)
    return result.stdout

def viewcat(isgenre, isasce, term):
    command = f"java -cp /home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../classes:/home/csmajs/jlee1442/cs166_project_phase3/python/java/scripts/../lib/pg73jdbc3.jar GameRental jlee1442_project_phase_3_DB 21420 jlee1442 5 {isgenre} {isasce} '{term}'"
    print(command)
    result = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)
    print(result)
    return result.stdout


@app.route('/')
def index():
    return render_template('index.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        g = getlogin(username, password)
        print(g)
        global USERISGLOBAL
        USERISGLOBAL = username
        if(g):
            return redirect(f'/dashboard?GLOBALUSER={username}')
        else:
            print('Invalid username or password. Please try again.')
    return render_template('login.html')

@app.route('/create_user', methods=['GET', 'POST'])
def create_user():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        phone = request.form['phone']
        created = createUser(username, password, phone) 
        if(created):
           return redirect(f'/')
    return render_template('create_user.html')

@app.route('/dashboard')
def dashboard():
    # Options for the dashboard
    GLOBALUSER = request.args.get('GLOBALUSER')
    return render_template('dashboard.html', GLOBALUSER=USERISGLOBAL)

@app.route('/profile')
def profile():
    # Options for the dashboard
    GLOBALUSER = request.args.get('GLOBALUSER')
    g = getprofile(GLOBALUSER)
    print(g)
    p = g.split('|')

    games = p[0].strip()
    phone = p[1].strip()
    overdue = p[2].strip()

    return render_template('profile.html', favorite_games=games, phone_number=phone, overdue_games=overdue)

@app.route('/updateprofile')
def updateprofile():
    # Options for the dashboard
    GLOBALUSER = request.args.get('GLOBALUSER')
    return render_template('updateprofile.html', GLOBALUSER=GLOBALUSER)


@app.route('/changefavoritegames', methods=['GET', 'POST'])
def changefavoritegames():
    if request.method == 'POST':
        game_name = request.form['game_name']
        operation = int(request.form['operation'])
        changefav(USERISGLOBAL, operation+1, game_name)

        return redirect('/dashboard') 
    else:
        return render_template('changefavoritegames.html')

@app.route('/changepassword', methods=['GET', 'POST'])
def changepassword():
    GLOBALUSER = request.args.get('GLOBALUSER')
    if request.method == 'POST':
        new_password = request.form['new_password']
        
        print(changepwd(USERISGLOBAL, new_password))
        # Logic to update the password goes here
        return redirect('/login')
    else:
        return render_template('changepassword.html', GLOBALUSER=GLOBALUSER)

@app.route('/viewcatalog', methods=['GET', 'POST'])
def viewcatalog():
    if request.method == 'POST':
        searchterms = request.form['searchterms']
        operation = int(request.form['operation'])
        operation1 = int(request.form['operation1'])
        generate_html_table(viewcat(operation + 1, operation1 + 1, searchterms))
        return generate_html_table(viewcat(operation + 1, operation1 + 1, searchterms))

    else:
        return render_template('viewcatalog.html')


if __name__ == '__main__':
    app.run(debug=True, port=9075)

