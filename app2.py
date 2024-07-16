from flask import Flask, request, jsonify
import face_recognition
import os
import numpy as np
import math

app = Flask(__name__)

# Load known face encodings
known_face_encodings = []
known_face_names = []


# Assume we have preloaded known known_faces in a folder called 'known_faces'
def load_known_faces():
    for image_name in os.listdir('known_faces'):
        image_path = os.path.join('known_faces', image_name)
        known_image = face_recognition.load_image_file(image_path)
        known_face_encoding = face_recognition.face_encodings(known_image)[0]
        known_face_encodings.append(known_face_encoding)
        known_face_names.append(image_name)


def face_confidence(face_distance, face_match_threshold=0.6):
    range = (1.0 - face_match_threshold)
    linear_val = (1.0 - face_distance) / (range * 2.0)

    if face_distance > face_match_threshold:
        return str(round(linear_val * 100, 2)) + '%'
    else:
        value = (linear_val + ((1.0 - linear_val) * math.pow((linear_val - 0.5) * 2, 0.2))) * 100
        return str(round(value, 2)) + '%'


@app.route('/upload', methods=['POST'])
def upload_file():
    if 'file' not in request.files:
        return jsonify({"error": "No file part"})
    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"})

    # Save the uploaded image
    file_path = os.path.join('uploaded_images', file.filename)
    file.save(file_path)

    # Load the uploaded image
    uploaded_image = face_recognition.load_image_file(file_path)
    uploaded_face_encodings = face_recognition.face_encodings(uploaded_image)

    if len(uploaded_face_encodings) == 0:
        return jsonify({"error": "No face found in the uploaded image"})

    uploaded_face_encoding = uploaded_face_encodings[0]

    # Compare known_faces
    face_distances = face_recognition.face_distance(known_face_encodings, uploaded_face_encoding)
    best_match_index = np.argmin(face_distances)
    match_percentage = face_confidence(face_distances[best_match_index])

    return jsonify({"match_percentage": match_percentage})


if __name__ == '__main__':
    if not os.path.exists('uploaded_images'):
        os.makedirs('uploaded_images')
    load_known_faces()
    app.run(host='0.0.0.0', port=5000)
