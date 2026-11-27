# Simple File Explorer

A fast, read-only file explorer (Java 17+, Swing). It shows each item's name, type, size,
path, creation date and last-modified date. Double-clicking a file opens it in its default app.

## Build & run

```
javac --release 17 -d out $(find src -name "*.java")
java -cp out explorer.Main
```

On Windows (PowerShell):

```
javac --release 17 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out explorer.Main
```

## How the input box works

| You type | What happens |
|---|---|
| A path (`C:\Users`, `/home/me`, `./docs`, `..`, `~`) | Opens that folder (or shows that one file) |
| A name (`report`) | Finds every file/folder whose name **contains** it (case-insensitive) in the current folder and all subfolders |
| A name + **Search all drives** ticked | Same, across every drive |

Input counts as a path if it contains `/` or `\`, starts with `~`, is `.` or `..`, or starts with a drive letter (`C:`).
**Stop** cancels a running search.

## Modules

```
src/explorer/
├── Main.java                 entry point
├── gui/        MainWindow, ResultsTable,         user input + display
│               FileTableModel
├── search/     SearchService                     interprets input, runs listing/search
├── file/       FileController, FileInfo,         the ONLY code that reads the filesystem
│               FileEntry, FolderEntry, FileAccessException
├── sidebar/    SidebarPanel, RecentHistory       drives, 5 recent items, 5 recent searches
├── error/      ErrorHandler, AppException        user messages + log file
└── ui/         Theme, Icons, Widgets,            shared look: colors, fonts, icons,
                ThinScrollBarUI                   styled buttons/fields (no app logic)
```

Dependencies only go one way: `gui → search → file`, `gui → sidebar`, `gui`/`sidebar → ui`, and everyone → `error`.

To change the colors or font, edit `src/explorer/ui/Theme.java`.

- **Recent history** is kept between runs using Java's per-user preferences store.
- **Error log:** `~/.simple-explorer/error.log`
