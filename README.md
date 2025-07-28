# Paidly — Smart Payment Reminder App

Paidly is a modern Android application developed using Jetpack Compose, designed to help users manage, track, and receive reminders for payments—whether they are dues to be paid or collected. With a color-coded UI, recurring reminder support, and notification alerts, Paidly simplifies personal finance tracking in an efficient and intuitive way.

---

## Features

### 1. Add, Edit, and Delete Reminders
- Create reminders with essential details: name, amount, due date, and type (To Pay / To Receive)
- Modify or delete reminders at any time
- Undo support via snackbar for accidental actions

### 2. Recurring Reminders
- Supports recurring intervals: None, Daily, Weekly, and Monthly
- Automatically generates the next instance of a reminder upon marking it as "Received"

### 3. Color-Coded Grid UI
- Red cards: Payments due within 3 days  
- Orange cards: Payments due within 2 weeks  
- Green cards: Payments due beyond 2 weeks  
- Uses a responsive `LazyVerticalGrid` layout for better organization

### 4. Navigation Drawer with Filtered Views
- Toggle between different reminder types:
  - All Reminders
  - To Pay
  - To Receive
  - History (Completed)
  - Notification Settings

### 5. History Page
- View all completed reminders that have been marked as "Received"
- Data is stored and managed using Room for persistence

### 6. Detailed Reminder View
- Tap on any reminder card to view a detailed bottom sheet
- Includes full information, an editable notes field, and options to mark as received or delete

### 7. Daily Notification Reminders
- Users can configure a preferred daily time for receiving reminders
- Notifications are delivered once daily, even if the app is not running
- Handled using WorkManager and NotificationChannel APIs

### 8. Boot Persistence
- Reminders are automatically re-scheduled after device reboot using a Boot BroadcastReceiver

---


## 📸 App Screenshots

<p align="center">
  <img src="screenshots/1.png" width="250" style="margin: 50px;"/>
  <img src="screenshots/2.png" width="250" style="margin: 50px;"/>
  <img src="screenshots/4.png" width="250" style="margin: 50px;"/>
  <img src="screenshots/3.png" width="250" style="margin: 50px;"/>
  <img src="screenshots/5.png" width="250" style="margin: 50px;"/>
</p>
---


## Tech Stack

| Component         | Technology                         |
|-------------------|-------------------------------------|
| UI                | Jetpack Compose, Material3          |
| Architecture      | MVVM                                |
| Navigation        | Jetpack Navigation Compose          |
| Database          | Room (SQLite ORM)                   |
| Background Tasks  | WorkManager with CoroutineWorker    |
| Preferences       | SharedPreferences                   |
| Notifications     | NotificationCompat, NotificationChannel |
| Language          | Kotlin                              |
| Min SDK           | 26 (Android 8.0)                    |

---

## Debugging and Testing

- Logs are filtered using the `PaidlyNotif` tag for easier debugging
- Logging includes:
  - WorkManager execution status
  - Time match checks
  - Reminder fetch counts
- Manual invocation of `scheduleDailyReminder()` is supported for testing
- App behavior has been validated for:
  - Background and killed states
  - Device restarts
  - Avoiding duplicate daily notifications

---

## Folder Structure

```plaintext
com.example.paidly
│
├── data
│   ├── local                  # Room database setup
│   │   ├── PaidlyDatabase.kt
│   │   ├── PaymentReminderDao.kt
│   │   └── PaymentReminderEntity.kt
│   └── model                  # Data models/entities
│
├── navigation                 # Navigation graphs and components
│   ├── AppNavigation.kt
│   ├── NavigationDrawerContent.kt
│   └── Screen.kt
│
├── ui                         # User Interface components
│   ├── components             # Reusable UI elements
│   ├── screens                # Individual screens
│   │   ├── HistoryScreen.kt
│   │   ├── HomeScreen.kt
│   │   └── NotificationSettingsScreen.kt
│   └── theme                  # Theming (colors, typography)
│
├── viewmodel                  # ViewModels for UI logic
│   ├── HomeViewModel.kt
│   └── HomeViewModelFactory.kt
│
├── utils                      # Utility classes and background tasks
│   ├── BootReceiver.kt
│   ├── NotificationPreferenceManager.kt
│   ├── NotificationScheduler.kt
│   └── NotificationUtils.kt
│   └── NotificationWorker.kt
│
└── MainActivity.kt            # Main application activity
```
---

## Permissions Used

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

---

## Future Enhancements

- Google Calendar integration
- Reminder snooze functionality
- Cloud sync support (e.g., Firebase, Supabase)
- Export data as Excel or CSV
- Biometric authentication for security

---
## 📥 How to Clone and Run

```bash
# Clone the repository
git clone https://github.com/Pulkit-10-0/Paidly.git

# Open the project in Android Studio
# Let Gradle sync and dependencies resolve

# Run the app on an emulator or Android device
```
---

## License
 
This project welcomes collaboration and feedback. Feel free to fork the repository or open issues and pull requests.
