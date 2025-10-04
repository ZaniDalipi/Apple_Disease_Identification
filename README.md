# Apple Disease Identification

> Mobile app (Android) for identifying diseases in apple leaves using a trained image-classification model.

## Table of Contents

* [About](#about)
* [Features](#features)
* [Repository Structure](#repository-structure)
* [Requirements](#requirements)
* [Installation](#installation)
* [Usage](#usage)

  * [Running the App](#running-the-app)
  * [Using the Model (if present)](#using-the-model-if-present)
* [Dataset](#dataset)
* [Model Training](#model-training)
* [Evaluation](#evaluation)
* [Architecture & Notes](#architecture--notes)
* [Testing](#testing)
* [Contributing](#contributing)
* [License](#license)
* [Acknowledgements](#acknowledgements)

---

## About

This repository contains an Android application (Kotlin) that demonstrates identification of common apple leaf diseases from images. The app integrates an image classification model (TensorFlow Lite / ONNX / other — check `/app` or `assets` for the exact model file) and provides a simple UI to capture or pick a photo and display predicted disease classes.

If you landed here expecting miracles, this README will at least get you a working local build and tell the model where to hide.

## Features

* Take photo or select image from gallery
* Perform on-device inference using a lightweight image classification model
* Show top predictions with confidence scores
* Simple UI built with Kotlin for Android

## Repository Structure

```
Apple_Disease_Identification/
├─ .idea/
├─ app/                  # Android app module (Kotlin)
├─ gradle/               # Gradle wrapper
├─ build.gradle
├─ settings.gradle
├─ gradlew
├─ gradlew.bat
└─ README.md
```

> Note: If you expect a `models/`, `assets/`, or `README_model.md` folder and don’t see it, the model may be stored inside `app/src/main/assets/` or was not included in this repo. Look there first.

## Requirements

* Android Studio (Arctic Fox or newer recommended)
* JDK 11+
* Gradle (wrapper provided)
* A device or emulator running Android API level compatible with the project (check `app/build.gradle` for `minSdkVersion` and `targetSdkVersion`).

## Installation

1. Clone the repository:

```bash
git clone https://github.com/ZaniDalipi/Apple_Disease_Identification.git
cd Apple_Disease_Identification
```

2. Open the project in Android Studio (`File -> Open`) and let Gradle sync.
3. If Android Studio prompts to install missing SDK components, accept them.

If there is a bundled model you'll find it in `app/src/main/assets/` or `app/src/main/res/raw/`. If not, get a `.tflite` or similar model and place it there.

## Usage

### Running the App

* Build and run from Android Studio on a device or emulator.
* Grant camera and storage permissions when prompted.
* Use the UI to capture or choose an image of an apple leaf.
* The app will run inference locally and show predictions.

### Using the Model (if present)

* The app likely loads a small image-classifier model from `assets/`. Search for code referencing `Interpreter`, `tflite`, `Model`, `TensorImage`, or similar in the Android code (e.g., `app/src/main/java/...`).
* If you replace the model, ensure input size and preprocessing in the app match the model (image size, normalization, channel order).

## Dataset

The original dataset is not included here. Typical datasets used for apple disease classification include PlantVillage or custom-collected images for classes such as:

* Apple Scab
* Black Rot
* Cedar Apple Rust
* Healthy

If you plan to retrain, collect a balanced dataset and apply augmentation for robustness (rotation, scaling, color jitter, random crop).

## Model Training

This repo does not include training scripts by default. If you train your own model, follow these suggestions:

1. Use a transfer learning approach with a lightweight backbone (MobileNetV2, EfficientNet-lite, or similar).
2. Train to convergence on a GPU-enabled environment (PyTorch or TensorFlow).
3. Export a quantized TFLite model for smaller size and lower latency on mobile. Example steps (TensorFlow):

```bash
# Example pseudocode
python train.py --dataset /path/to/data --model mobilenet_v2 --epochs 30
# Then convert
tflite_convert --saved_model_dir=saved_model --output_file=model.tflite --optimizations=DEFAULT
```

4. Verify input shape and preprocessing pipeline.

## Evaluation

Evaluate with standard metrics:

* Accuracy (per-class & overall)
* Confusion matrix (to spot class confusion)
* Precision / recall / F1 per class

For mobile, also measure:

* Inference latency (ms)
* Model size (MB)
* On-device memory usage and battery impact

## Architecture & Notes

* Frontend: Kotlin Android app
* Model:  TFLite or embedded model inside the app - tested on data from my apple orchard just for demostration as final project on my university
* Preprocessing: resize + normalize to match model's expected input

Look into `app/src/main` for the activity and model-loading code. The prediction pipeline usually:

1. Acquire image
2. Resize & normalize
3. Run inference
4. Map output indices to labels
5. Display top-k predictions

## Testing

* Unit tests: none included by default. Add UI tests (Espresso) for camera/gallery flows and mock model runner for deterministic outputs.
* Manual tests: test with various lighting conditions and backgrounds.

## Contributing

If you want to contribute (heroic, but also mildly delusional), please:

1. Fork the repo
2. Create a branch `feature/your-feature`
3. Open a PR with a clear description

Make sure to include reproducible steps and test data or synthetic examples.

## License

If no `LICENSE` file exists in the repo, treat this project as "all rights reserved" by the author. Add an OSI license file if you want others to reuse it.

## Acknowledgements

* Plant disease datasets and research papers on plant pathology and transfer learning.
* Android and TensorFlow Lite communities for mobile ML best practices.

---

If you want, I can also generate a trimmed `README.md` version tailored for GitHub with badges, or a separate `MODEL.md` with exact instructions for training and converting a model to TFLite. But you looked busy, so I already did the heavy lifting. You're welcome.
