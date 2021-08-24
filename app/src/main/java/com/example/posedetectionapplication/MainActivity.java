package com.example.posedetectionapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Objects;

import static java.lang.Math.atan2;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button btnImg;
    String chck = "";
    private static final int RC_SIGN_IN = 1001;
    Bitmap bitmap;
    AccuratePoseDetectorOptions options;
    InputImage image;
    TextView angleis;

    BitmapFactory.Options myOptions;
    Canvas canvas;
    Bitmap mutableBitmap;
    Bitmap workingBitmap;
    Paint paint;
    ObjectDetectorOptions optionsare;
    ImageView imageView1, imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView = findViewById(R.id.image);
        btnImg = findViewById(R.id.btnImg);
        angleis = findViewById(R.id.angleis);
        imageView1 = findViewById(R.id.imagecolr);
        imageView2 = findViewById(R.id.imagecolr2);

        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnableRuntimePermission();
            }
        });
        // Accurate pose detector on static images, when depending on the pose-detection-accurate sdk
        options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                        .build();

// Multiple object detection in static images
        optionsare =
                new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()

                        .enableClassification()
                        .build();


    }


    public void EnableRuntimePermission() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                isStoragePermissionGranted();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }

    public void isStoragePermissionGranted() {
        showPictureDialog();
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(Objects.requireNonNull(this));
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        chck = "image";
        startActivityForResult(galleryIntent, 1);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        chck = "image";

        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (chck.equalsIgnoreCase("image")) {
            if (requestCode == 1) {
            } else if (requestCode == 2) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(thumbnail);

                image = InputImage.fromBitmap(thumbnail, 360);

                PoseDetector poseDetector = PoseDetection.getClient(options);
                Task<Pose> result =
                        poseDetector.process(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<Pose>() {
                                            @Override
                                            public void onSuccess(Pose pose) {
                                                Toast.makeText(MainActivity.this, "Pose Detected", Toast.LENGTH_SHORT).show();
                                                List<PoseLandmark> allPoseLandmarks = pose.getAllPoseLandmarks();

                                                PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);

                                                PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
                                                PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
                                                PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
                                                PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
                                                PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
                                                PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
                                                PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
                                                PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
                                                PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
                                                PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
                                                PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);
                                                PoseLandmark leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY);
                                                PoseLandmark rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY);
                                                PoseLandmark leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX);
                                                PoseLandmark rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX);
                                                PoseLandmark leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB);
                                                PoseLandmark rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB);
                                                PoseLandmark leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL);
                                                PoseLandmark rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL);
                                                PoseLandmark leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX);
                                                PoseLandmark rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX);
                                                PoseLandmark nose = pose.getPoseLandmark(PoseLandmark.NOSE);
                                                PoseLandmark leftEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER);
                                                PoseLandmark leftEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE);
                                                PoseLandmark leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER);
                                                PoseLandmark rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER);
                                                PoseLandmark rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE);
                                                PoseLandmark rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER);
                                                PoseLandmark leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR);
                                                PoseLandmark rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR);
                                                PoseLandmark leftMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH);
                                                PoseLandmark rightMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH);
                                                double rightHipAngle = getAngle(
                                                        leftHip,
                                                        leftWrist,
                                                        leftShoulder);


                                                angleis.setText("Angle: " + rightHipAngle);
                                                myOptions = new BitmapFactory.Options();

                                                paint = new Paint();
                                                paint.setAntiAlias(true);
                                                paint.setColor(Color.WHITE);
                                                workingBitmap = Bitmap.createBitmap(thumbnail);
                                                mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
                                                canvas = new Canvas(mutableBitmap);
//                                                drawpoint(imageView, leftEye.getPosition3D().getX(), leftEye.getPosition3D().getY(), 5);
                                                if (rightEye != null) {
                                                    drawpoint(imageView, rightEye.getPosition3D().getX(), rightEye.getPosition3D().getY(), 3);
                                                }
                                                if (nose != null) {
                                                    drawpoint(imageView, nose.getPosition3D().getX(), nose.getPosition3D().getY(), 3);
                                                }
                                                if (leftEye != null) {
                                                    drawpoint(imageView, leftEye.getPosition3D().getX(), leftEye.getPosition3D().getY(), 3);
                                                }
                                                if (leftMouth != null) {
                                                    drawpoint(imageView, leftMouth.getPosition3D().getX(), leftMouth.getPosition3D().getY(), 3);
                                                }
                                                if (rightMouth != null) {
                                                    drawpoint(imageView, rightMouth.getPosition3D().getX(), rightMouth.getPosition3D().getY(), 3);
                                                }
                                                if (rightEar != null) {
                                                    drawpoint(imageView, rightEar.getPosition3D().getX(), rightEar.getPosition3D().getY(), 3);
                                                }
                                                if (leftEar != null) {
                                                    drawpoint(imageView, leftEar.getPosition3D().getX(), leftEar.getPosition3D().getY(), 3);
                                                }
                                                if (rightShoulder != null) {
                                                    drawpoint(imageView, rightShoulder.getPosition3D().getX(), rightShoulder.getPosition3D().getY(), 3);
                                                }
                                                if (leftShoulder != null) {
                                                    drawpoint(imageView, leftShoulder.getPosition3D().getX(), leftShoulder.getPosition3D().getY(), 3);
                                                }
                                                if (leftElbow != null) {
                                                    drawpoint(imageView, leftElbow.getPosition3D().getX(), leftElbow.getPosition3D().getY(), 3);
                                                }
                                                if (rightElbow != null) {
                                                    drawpoint(imageView, rightElbow.getPosition3D().getX(), rightElbow.getPosition3D().getY(), 3);
                                                }
                                                if (leftWrist != null) {
                                                    drawpoint(imageView, leftWrist.getPosition3D().getX(), leftWrist.getPosition3D().getY(), 3);
                                                }
                                                if (rightWrist != null) {
                                                    drawpoint(imageView, rightWrist.getPosition3D().getX(), rightWrist.getPosition3D().getY(), 3);
                                                }
                                                if (leftAnkle != null) {
                                                    drawpoint(imageView, leftAnkle.getPosition3D().getX(), leftAnkle.getPosition3D().getY(), 3);
                                                }
                                                if (rightAnkle != null) {
                                                    drawpoint(imageView, rightAnkle.getPosition3D().getX(), rightAnkle.getPosition3D().getY(), 3);
                                                }
                                                ObjectDetector objectDetector = ObjectDetection.getClient(optionsare);
                                                InputImage image = InputImage.fromBitmap(thumbnail, 0);
                                                objectDetector.process(image)
                                                        .addOnSuccessListener(
                                                                new OnSuccessListener<List<DetectedObject>>() {
                                                                    @Override
                                                                    public void onSuccess(List<DetectedObject> detectedObjects) {
                                                                        Paint paint = new Paint();
                                                                        paint.setColor(Color.YELLOW);
                                                                        paint.setStrokeWidth(1);    // Task completed successfully
                                                                        paint.setStyle(Paint.Style.STROKE);    // Task completed successfully
                                                                        Toast.makeText(MainActivity.this, "DPONE", Toast.LENGTH_SHORT).show();
                                                                        for (DetectedObject detectedObject : detectedObjects) {
                                                                            Rect boundingBox = detectedObject.getBoundingBox();

                                                                            drawprect(imageView, boundingBox, paint);
                                                                        } // ...
                                                                    }
                                                                })
                                                        .addOnFailureListener(
                                                                new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Task failed with an exception
                                                                        // ...
                                                                        Log.e("TGED", "e" + e);
                                                                        Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("TGED", "EXCEPTION: " + e.getLocalizedMessage());
                                            }
                                        });
                Palette.Builder p = new Palette.Builder(thumbnail);
                p.generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@Nullable Palette palette) {
                        String[] strings = palette.toString().split("@");
                        Log.e("TGED", "STRINF" + palette.toString());//                        imageView1.setBackgroundColor(Integer.valueOf(strings[1]));
                        Log.e("TGED", "STRINF" + strings[1]);//
                        String s = "#" + strings[1];
                        Log.e("TGED", "s" + s);//
                        int color = Integer.parseInt(strings[1], 16)+0xFF000000;
                        imageView1.setBackgroundColor(color);
//                        imageView2 = findViewById(R.id.imagecolr2);

                    }
                });
            }
        }


    }


    static double getAngle
            (PoseLandmark
                     firstPoint, PoseLandmark
                     midPoint, PoseLandmark
                     lastPoint) {
        double result =
                Math.toDegrees(
                        atan2(lastPoint.getPosition().y - midPoint.getPosition().y,
                                lastPoint.getPosition().x - midPoint.getPosition().x)
                                - atan2(firstPoint.getPosition().y - midPoint.getPosition().y,
                                firstPoint.getPosition().x - midPoint.getPosition().x));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }

    private void drawpoint
            (ImageView
                     imageView,
             float x,
             float y,
             int raduis) {
        myOptions.inDither = false;
        myOptions.inScaled = false;

        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
        myOptions.inPurgeable = true;
//  ArrayList<Point> list= new ArrayList<>();
        canvas.drawCircle(x, y, raduis, paint);

        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(mutableBitmap);
    }

    private void drawprect
            (ImageView
                     imageView, Rect
                     rect, Paint
                     paint) {

        canvas.drawRect(rect, paint);

        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(mutableBitmap);
    }


}