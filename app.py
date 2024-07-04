# @app.route('/Home')
# def hello():
#     return "hello"

from flask import Flask, request, jsonify
import face_recognition
import numpy as np
import os

app = Flask(__name__)

reference_image_path = 'faces/aditya.jpg'
reference_image = face_recognition.load_image_file(reference_image_path)
reference_encoding = face_recognition.face_encodings(reference_image)[0]

def face_confidence(face_distance, face_match_threshold=0.6):
    range = (1.0 - face_match_threshold)
    linear_val = (1.0 - face_distance) / (range * 2.0)

    if face_distance > face_match_threshold:
        return str(round(linear_val * 100, 2)) + '%'
    else:
        value = (linear_val + ((1.0 - linear_val) * np.power((linear_val - 0.5) * 2, 0.2))) * 100
        return str(round(value, 2)) + '%'

@app.route('/upload', methods=['POST'])
def upload_image():
    if 'file' not in request.files:
        return jsonify({'error': 'No file provided'}), 400

    file = request.files['file']
    image_path = os.path.join('uploads', file.filename)
    file.save(image_path)

    uploaded_image = face_recognition.load_image_file(image_path)
    uploaded_encoding = face_recognition.face_encodings(uploaded_image)

    if len(uploaded_encoding) == 0:
        return jsonify({'error': 'No face found in the image'}), 400

    uploaded_encoding = uploaded_encoding[0]

    # Compare the reference image with the uploaded image
    results = face_recognition.compare_faces([reference_encoding], uploaded_encoding)
    face_distances = face_recognition.face_distance([reference_encoding], uploaded_encoding)
    matching_percentage = face_confidence(face_distances[0])

    return jsonify({'match': results[0], 'matching_percentage': matching_percentage})

if __name__ == '__main__':
    if not os.path.exists('uploads'):
        os.makedirs('uploads')
    app.run(debug=True)



