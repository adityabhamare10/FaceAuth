from flask import Flask, request, jsonify
import face_recognition
import os
import mysql.connector
from mysql.connector import Error

app = Flask(__name__)

def create_connection():
    return mysql.connector.connect(
        host='localhost',
        user='root',
        password='harshaLi#123',
        database='novus_ark'
    )

def get_image_name_by_emp_id(emp_id):
    try:
        conn = create_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT emp_image FROM user_table WHERE emp_id=%s", (emp_id,))
        row = cursor.fetchone()
        conn.close()
        return row[0] if row else None
    except Error as e:
        print(f"Database error: {e}")
        return None

def face_confidence(face_distance, face_match_threshold=0.6):
    range = (1.0 - face_match_threshold)
    linear_val = (1.0 - face_distance) / (range * 2.0)

    if face_distance > face_match_threshold:
        return str(round(linear_val * 100, 2)) + '%'
    else:
        value = (linear_val + ((1.0 - linear_val) * (linear_val - 0.5) * 2)) * 100
        return str(round(value, 2)) + '%'

@app.route('/upload', methods=['POST'])
def upload_file():
    if 'file' not in request.files or 'emp_id' not in request.form:
        return jsonify({"error": "Missing file or form data"})

    file = request.files['file']
    employee_id = request.form['emp_id']

    if file.filename == '':
        return jsonify({"error": "No selected file"})

    file_path = os.path.join('uploaded_images', file.filename)
    file.save(file_path)

    known_image_name = get_image_name_by_emp_id(employee_id)
    if not known_image_name:
        return jsonify({"error": "Employee ID not found in database"})

    known_image_path = os.path.join('known_faces', known_image_name)
    if not os.path.exists(known_image_path):
        return jsonify({"error": "Known image not found on server"})

    known_image = face_recognition.load_image_file(known_image_path)
    known_face_encoding = face_recognition.face_encodings(known_image)[0]

    uploaded_image = face_recognition.load_image_file(file_path)
    uploaded_face_encodings = face_recognition.face_encodings(uploaded_image)

    if len(uploaded_face_encodings) == 0:
        return jsonify({"error": "No face found in the uploaded image"})

    uploaded_face_encoding = uploaded_face_encodings[0]

    face_distance = face_recognition.face_distance([known_face_encoding], uploaded_face_encoding)[0]
    match_percentage = face_confidence(face_distance)

    return jsonify({"match_percentage": match_percentage})

if __name__ == '__main__':
    if not os.path.exists('uploaded_images'):
        os.makedirs('uploaded_images')
    app.run(host='0.0.0.0', port=5000)
