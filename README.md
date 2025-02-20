# VapeShop

## About
VapeShop is a demo Android application for a vape shop. It serves as a learning project and portfolio piece, showcasing basic e-commerce functionality integrated with Firebase and Yandex Maps. The app is built using Clean Architecture and MVVM.

---

## Features


- **Product Catalog:**
  - Browse product categories.
  - View products within each category.
  - Add products to the cart.

- **Cart Management:**
  - View items in the cart.
  - Increase or decrease product quantity.
  - Remove products from the cart.
  - Checkout with options for delivery or pickup.
  - Select payment method (cash on delivery or online; online payment is currently a stub).

- **User Profile:**
  - View order history with statuses (processing, received, cancelled).
  - Edit profile (first name, last name, phone number, delivery address).
  - Log out.
  - Log in or register via email and password.

---

## Technologies

- **Architecture:** Clean Architecture + MVVM (note: this is a learning implementation, and some parts might need improvements)
- **UI Layout:** XML
- **Image Loading:** Glide
- **Dependency Injection:** Hilt
- **Backend:** Firebase (some features are implemented with temporary workarounds)
- **Navigation:** Android Navigation Component
- **Asynchronous Programming:** Coroutine + Flow
- **Local Storage:** Room
- **Maps:** Yandex Maps (for selecting delivery addresses)

---

## Requirements

- **Minimum Android Version:** Android 12 (API 31)
- **Yandex Maps API Key:**  
  For the app to function correctly, you must specify your Yandex Maps API key in the `local.properties` file.  
  Example:
    ```properties
    yandexMapApiKey=YOUR_API_KEY
---

## Installation & Running
1. **Clone the repository:**

   ```bash
   git clone https://github.com/SemkaHub/vapeshop.git

2. **Open the Project**  
   Open the project in Android Studio.

3. **Set Up Yandex Maps API Key**  
   To run the app, add your Yandex Maps API key in the `local.properties` file:
     ```properties
     yandexMapApiKey=YOUR_API_KEY

4. **Build and Run**  
Build the project in Android Studio and run it on a physical device or emulator.
