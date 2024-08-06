import 'dart:io';
import 'package:flutter/material.dart';
import 'package:camera/camera.dart';
import 'package:path_provider/path_provider.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:fluttertoast/fluttertoast.dart';

class AuthScreenReverse extends StatefulWidget {
  @override
  _AuthScreenState createState() => _AuthScreenState();
}

class _AuthScreenState extends State<AuthScreenReverse> {
  CameraController? controller;
  String percentage = "Percentage: ";
  String empId = "Emp ID: ";
  String? currentPhotoPath;

  @override
  void initState() {
    super.initState();
    initializeCamera();
  }

  Future<void> initializeCamera() async {
    final cameras = await availableCameras();
    controller = CameraController(cameras.first, ResolutionPreset.medium);
    await controller!.initialize();
    setState(() {});
  }

  Future<void> capturePhoto() async {
    if (controller == null || !controller!.value.isInitialized) {
      Fluttertoast.showToast(
        msg: "Camera not initialized",
        toastLength: Toast.LENGTH_SHORT,
        gravity: ToastGravity.BOTTOM,
      );
      return;
    }

    final directory = await getApplicationDocumentsDirectory();
    final path = '${directory.path}/${DateTime.now().millisecondsSinceEpoch}.jpg';
    currentPhotoPath = path;

    await controller!.takePicture().then((XFile file) {
      file.saveTo(path);
    });

    setState(() {
      percentage = "Photo captured";
    });

    Fluttertoast.showToast(
      msg: "Photo saved to local storage",
      toastLength: Toast.LENGTH_SHORT,
      gravity: ToastGravity.BOTTOM,
    );
  }

  Future<void> uploadPhoto() async {
    if (currentPhotoPath == null) {
      setState(() {
        percentage = "No photo to upload";
      });
      Fluttertoast.showToast(
        msg: "No photo to upload",
        toastLength: Toast.LENGTH_SHORT,
        gravity: ToastGravity.BOTTOM,
      );
      return;
    }

    final file = File(currentPhotoPath!);
    final request = http.MultipartRequest('POST', Uri.parse('http://192.168.86.35:5000/upload'));
    request.files.add(await http.MultipartFile.fromPath('file', file.path));

    final response = await request.send();
    if (response.statusCode == 200) {
      final responseData = await response.stream.bytesToString();
      final jsonResponse = jsonDecode(responseData);

      if (jsonResponse.containsKey('emp_id') && jsonResponse.containsKey('match_percentage')) {
        final empIdResponse = jsonResponse['emp_id'];
        final matchPercentage = jsonResponse['match_percentage'];
        setState(() {
          empId = "Emp ID: $empIdResponse";
          percentage = "Percentage: $matchPercentage";
        });
      } else if (jsonResponse.containsKey('error')) {
        final error = jsonResponse['error'];
        setState(() {
          percentage = "Error: $error";
        });
      }

      Fluttertoast.showToast(
        msg: "Photo uploaded successfully",
        toastLength: Toast.LENGTH_SHORT,
        gravity: ToastGravity.BOTTOM,
      );
    } else {
      setState(() {
        percentage = "Error: ${response.reasonPhrase}";
      });

      Fluttertoast.showToast(
        msg: "Upload failed: ${response.reasonPhrase}",
        toastLength: Toast.LENGTH_SHORT,
        gravity: ToastGravity.BOTTOM,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Face Auth'),
      ),
      body: Column(
        children: [
          if (controller != null && controller!.value.isInitialized)
            Container(
              height: 400,
              width: 400,
              child: CameraPreview(controller!),
            )
          else
            CircularProgressIndicator(),
          SizedBox(height: 16),
          Text(empId),
          Text(percentage),
          SizedBox(height: 16),
          ElevatedButton(
            onPressed: capturePhoto,
            child: Text('Capture Photo'),
          ),
          ElevatedButton(
            onPressed: uploadPhoto,
            child: Text('Upload Photo'),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    controller?.dispose();
    super.dispose();
  }
}
