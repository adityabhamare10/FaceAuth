import os

from flask import Flask, request, jsonify

app = Flask(__name__)


@app.route('/test', methods=['POST'])
def test_endpoint():
    data = request.json
    if not data or 'message' not in data:
        return jsonify({'error': 'No message provided'}), 400

    return jsonify({'response': 'Message received: ' + data['message']}), 200


if __name__ == '__main__':
    if not os.path.exists('uploads'):
        os.makedirs('uploads')
    app.run(host='0.0.0.0', port=5000, debug=True)
