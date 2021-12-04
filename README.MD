# Token Manager
This is a custom fork of [Realized/TokenManager](https://github.com/Realizedd/TokenManager).

## Changes
- Updated to 1.17.1, may break support for other versions.
- Removed JSON Simple in place of Google GSON.
- Fixed dependency issues and removed update checker.
- Update MySQL Driver class to `com.mysql.cj.jdbc.Driver`.

### Disclaimer
I do not intend to maintain this fork.
The source code is available for anyone else that wishes to make changes or continue updating it.

### Compilation Instructions
- Command Prompt: `CALL gradlew.bat clean build`
- Shell: `./gradlew clean build`
