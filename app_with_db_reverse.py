from flask import Flask, request, jsonify
import face_recognition
import os
import mysql.connector
from mysql.connector import Error

app = Flask(__name__)

# Database connection
def create_connection():
    try:
        return mysql.connector.connect(
            host='localhost',
            user='root',
            password='harshaLi#123',
            database='novus_ark'
        )
    except Error as e:
        print(f"Database connection error: {e}")
        return None

def get_employee_id_by_image(image_name):
    try:
        conn = create_connection()
        if conn is None:
            return None

        cursor = conn.cursor()
        cursor.execute("SELECT emp_id FROM user_table WHERE emp_image = %s", (image_name,))
        result = cursor.fetchone()
        cursor.close()
        conn.close()
        return result[0] if result else None
    except Error as e:
        print(f"Database error: {e}")
        return None

def face_confidence(face_distance, face_match_threshold=0.6):
    range = (1.0 - face_match_threshold)
    linear_val = (1.0 - face_distance) / (range * 2.0)

    if face_distance > face_match_threshold:
        return round(linear_val * 100, 2)
    else:
        value = (linear_val + ((1.0 - linear_val) * (linear_val - 0.5) * 2)) * 100
        return round(value, 2)

@app.route('/upload', methods=['POST'])
def upload_file():
    if 'file' not in request.files:
        return jsonify({"error": "Missing file"}), 400

    file = request.files['file']

    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    # Save the uploaded image
    file_path = os.path.join('uploaded_images', file.filename)
    file.save(file_path)

    highest_match = {
        'image_name': None,
        'match_percentage': 0.0
    }

    # Load the uploaded image
    uploaded_image = face_recognition.load_image_file(file_path)
    uploaded_face_encodings = face_recognition.face_encodings(uploaded_image)

    if len(uploaded_face_encodings) == 0:
        return jsonify({"error": "No face found in the uploaded image"}), 400

    uploaded_face_encoding = uploaded_face_encodings[0]

    for known_image_name in os.listdir('known_faces'):
        known_image_path = os.path.join('known_faces', known_image_name)
        if not os.path.isfile(known_image_path):
            continue

        known_image = face_recognition.load_image_file(known_image_path)
        known_face_encodings = face_recognition.face_encodings(known_image)

        if len(known_face_encodings) == 0:
            continue

        known_face_encoding = known_face_encodings[0]

        # Compare faces
        face_distance = face_recognition.face_distance([known_face_encoding], uploaded_face_encoding)[0]
        match_percentage = face_confidence(face_distance)

        if match_percentage > highest_match['match_percentage']:
            highest_match = {
                'image_name': known_image_name,
                'match_percentage': match_percentage
            }

        # Check if match percentage is above 90%
        if match_percentage >= 90:
            emp_id = get_employee_id_by_image(known_image_name)
            print(emp_id)
            if emp_id:
                return jsonify({
                    "emp_id": emp_id,
                    "match_percentage": f"{match_percentage}%"
                })

    if highest_match['image_name'] is None:
        return jsonify({"error": "No matching faces found"}), 404

    return jsonify({
        "error": "No match found above 90%",
        "highest_match_image": highest_match['image_name'],
        "highest_match_percentage": f"{highest_match['match_percentage']}%"
    })

if __name__ == '__main__':
    if not os.path.exists('uploaded_images'):
        os.makedirs('uploaded_images')
    app.run(host='0.0.0.0', port=5000)
