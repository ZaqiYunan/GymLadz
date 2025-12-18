# How to Add Your Friend's Screen to GymLadz

## ✅ Git Repository Setup Complete!

Your Git repository is now initialized with all your current code committed.

---

## Method 1: Direct File Addition (Simplest)

### Step 1: Get Your Friend's File
Ask your friend to send you their Kotlin screen file (e.g., `WorkoutScreen.kt`)

### Step 2: Copy the File
Copy their file to:
```
d:\Towgas\Semester 5\GymLadz\app\src\main\java\com\example\gymladz\ui\screens\
```

### Step 3: Update MainActivity.kt

Add their screen to the navigation:

```kotlin
// In MainActivity.kt
import com.example.gymladz.ui.screens.WorkoutScreen  // Add this import

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
            0 -> ExerciseListScreen()
            1 -> ProfileScreen()
            2 -> SettingsScreen()
            3 -> CommunityScreen()
            4 -> WorkoutScreen()  // Add your friend's screen here
        }
        
        BottomNavBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}
```

### Step 4: Update BottomNavBar.kt

Add a new navigation icon:

```kotlin
// In BottomNavBar.kt, add this NavItem
NavItem(
    icon = "💪",  // Choose an appropriate emoji
    isSelected = selectedTab == 4,
    onClick = { onTabSelected(4) }
)
```

### Step 5: Commit the Changes

```powershell
cd "d:\Towgas\Semester 5\GymLadz"
git add .
git commit -m "Add [Friend's Name]'s WorkoutScreen"
```

---

## Method 2: Using GitHub (Professional Way)

### Step 1: Create GitHub Repository

1. Go to https://github.com/new
2. Repository name: `GymLadz`
3. Description: "Fitness tracking Android app"
4. Keep it Public or Private (your choice)
5. **DON'T** check "Initialize with README"
6. Click "Create repository"

### Step 2: Push Your Code to GitHub

```powershell
cd "d:\Towgas\Semester 5\GymLadz"
git remote add origin https://github.com/YOUR_USERNAME/GymLadz.git
git branch -M main
git push -u origin main
```

### Step 3: Share Repository with Your Friend

Send them the repository URL:
```
https://github.com/YOUR_USERNAME/GymLadz
```

### Step 4: Your Friend Clones and Adds Their Screen

**Your friend does:**
```powershell
git clone https://github.com/YOUR_USERNAME/GymLadz.git
cd GymLadz
git checkout -b add-workout-screen
```

Then they:
1. Create their screen file in `app/src/main/java/com/example/gymladz/ui/screens/`
2. Test it locally
3. Commit and push:
```powershell
git add .
git commit -m "Add WorkoutScreen"
git push origin add-workout-screen
```

### Step 5: Merge Their Work

**Option A: Via GitHub Pull Request**
1. Your friend creates a Pull Request on GitHub
2. You review it
3. Click "Merge Pull Request"
4. Pull the changes locally:
```powershell
git pull origin main
```

**Option B: Direct Merge**
```powershell
git pull origin add-workout-screen
git merge add-workout-screen
git push origin main
```

---

## Example: Adding a Friend's Screen

Let's say your friend created a `NutritionScreen.kt`. Here's exactly what to do:

### 1. Place the file:
```
d:\Towgas\Semester 5\GymLadz\app\src\main\java\com\example\gymladz\ui\screens\NutritionScreen.kt
```

### 2. Update MainActivity.kt:
```kotlin
import com.example.gymladz.ui.screens.NutritionScreen

// In MainScreen()
when (selectedTab) {
    0 -> ExerciseListScreen()
    1 -> ProfileScreen()
    2 -> SettingsScreen()
    3 -> CommunityScreen()
    4 -> NutritionScreen()  // NEW
}
```

### 3. Update BottomNavBar.kt:
```kotlin
// Add in the Row where other NavItems are
NavItem(
    icon = "🍎",
    isSelected = selectedTab == 4,
    onClick = { onTabSelected(4) }
)
```

### 4. Build and test!

---

## Quick Git Commands

```powershell
# See what changed
git status

# Add all changes
git add .

# Commit with message
git commit -m "Your message here"

# Push to GitHub
git push

# Pull latest changes
git pull

# See commit history
git log --oneline

# Undo last commit (keep changes)
git reset --soft HEAD~1
```

---

## Troubleshooting

### "Merge conflict" error
1. Open the conflicted file
2. Look for `<<<<<<<`, `=======`, `>>>>>>>` markers
3. Choose which code to keep
4. Remove the markers
5. `git add .` and `git commit`

### Friend's screen doesn't show up
- Check the import statement in MainActivity
- Make sure the file is in the correct directory
- Verify the screen name matches in the `when` statement
- Check that you added the NavItem in BottomNavBar

### Build errors after adding friend's screen
- Make sure their screen follows the same pattern as your screens
- Check for missing imports
- Verify they're using the same theme colors
- Try "Build → Clean Project" then rebuild

---

## Next Steps

1. ✅ Git repository is initialized
2. ⏳ Create GitHub repository (optional but recommended)
3. ⏳ Get your friend's screen file
4. ⏳ Add it to your project
5. ⏳ Update navigation
6. ⏳ Test and commit

**Ready to proceed!** Just let me know when you have your friend's file, and I'll help you integrate it.
