# Git Collaboration Guide for GymLadz

## Step 1: Initialize Git Repository

First, initialize a Git repository in your project:

```powershell
cd "d:\Towgas\Semester 5\GymLadz"
git init
```

## Step 2: Create .gitignore

Create a `.gitignore` file to exclude build files and IDE settings:

```
# Built application files
*.apk
*.ap_
*.aab

# Files for the ART/Dalvik VM
*.dex

# Java class files
*.class

# Generated files
bin/
gen/
out/
build/

# Gradle files
.gradle/
gradle-app.setting
!gradle-wrapper.jar

# Local configuration file (sdk path, etc)
local.properties

# Android Studio
*.iml
.idea/
.DS_Store
captures/
.externalNativeBuild
.cxx

# Keystore files
*.jks
*.keystore
```

## Step 3: Make Initial Commit

```powershell
git add .
git commit -m "Initial commit: GymLadz app with Exercise List, Profile, Settings, and Community screens"
```

## Step 4: Create GitHub Repository

1. Go to [GitHub](https://github.com)
2. Click "New repository"
3. Name it "GymLadz"
4. Don't initialize with README (you already have code)
5. Click "Create repository"

## Step 5: Connect to GitHub

```powershell
git remote add origin https://github.com/YOUR_USERNAME/GymLadz.git
git branch -M main
git push -u origin main
```

## Step 6: Collaborate with Your Friend

### Option A: Your Friend Creates a Branch (Recommended)

**Your friend should:**
1. Clone the repository:
   ```powershell
   git clone https://github.com/YOUR_USERNAME/GymLadz.git
   cd GymLadz
   ```

2. Create a new branch for their work:
   ```powershell
   git checkout -b friend-feature
   ```

3. Make their changes (e.g., create their screen)

4. Commit and push:
   ```powershell
   git add .
   git commit -m "Add [FeatureName] screen"
   git push origin friend-feature
   ```

5. Create a Pull Request on GitHub

**You should:**
1. Review the Pull Request on GitHub
2. Merge it if everything looks good
3. Pull the changes:
   ```powershell
   git pull origin main
   ```

### Option B: Direct File Sharing (Simpler for Beginners)

**Your friend sends you their Kotlin file:**
1. They send you their screen file (e.g., `WorkoutScreen.kt`)
2. You copy it to the appropriate directory:
   ```
   d:\Towgas\Semester 5\GymLadz\app\src\main\java\com\example\gymladz\ui\screens\
   ```
3. Add the screen to navigation in `MainActivity.kt`
4. Commit the changes:
   ```powershell
   git add .
   git commit -m "Add [Friend's Name]'s screen"
   git push
   ```

## Step 7: Merge Multiple Screens

If you have multiple team members, each creating one screen:

### In MainActivity.kt, add more navigation options:

```kotlin
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
            0 -> ExerciseListScreen()      // Your screen
            1 -> ProfileScreen()            // Your screen
            2 -> FriendScreen1()            // Friend 1's screen
            3 -> FriendScreen2()            // Friend 2's screen
            4 -> FriendScreen3()            // Friend 3's screen
        }
        
        BottomNavBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}
```

### Update BottomNavBar to have more items:

```kotlin
// Add more NavItems in BottomNavBar.kt
NavItem(icon = "📊", isSelected = selectedTab == 2, onClick = { onTabSelected(2) })
NavItem(icon = "📈", isSelected = selectedTab == 3, onClick = { onTabSelected(3) })
// etc.
```

## Quick Commands Reference

```powershell
# Check status
git status

# See changes
git diff

# Pull latest changes
git pull

# Add all changes
git add .

# Commit changes
git commit -m "Your message"

# Push to GitHub
git push

# See commit history
git log --oneline

# Create new branch
git checkout -b branch-name

# Switch branches
git checkout branch-name

# Merge branch
git merge branch-name
```

## Troubleshooting

### Merge Conflicts
If you get merge conflicts:
1. Open the conflicted file
2. Look for `<<<<<<<`, `=======`, `>>>>>>>` markers
3. Choose which code to keep
4. Remove the conflict markers
5. `git add .` and `git commit`

### Already have changes but no repo
```powershell
# Stash your changes
git stash

# Pull latest
git pull

# Apply your changes back
git stash pop
```

## Best Practices

1. **Commit often** with clear messages
2. **Pull before you push** to avoid conflicts
3. **Use branches** for new features
4. **Test before committing** to avoid breaking the build
5. **Communicate** with your team about who's working on what

## For Your Assignment

Since each team member creates one screen:

1. **Create the repo** (you do this)
2. **Share the repo link** with your team
3. **Each person clones** the repo
4. **Each person creates their screen** in a separate file
5. **Each person commits** their screen
6. **You merge** all screens in MainActivity
7. **Everyone pulls** the final version

Would you like me to help you initialize the Git repository now?
