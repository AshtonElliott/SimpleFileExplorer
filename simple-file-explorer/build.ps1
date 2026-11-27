# Builds Simple File Explorer into a Windows app:  dist\SimpleFileExplorer\SimpleFileExplorer.exe
#
# Run from PowerShell in this folder:
#     powershell -ExecutionPolicy Bypass -File .\build.ps1
#
# Needs a JDK 17+ (uses javac, jar and jpackage from the same JDK as "java").

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

# --- Find the JDK's bin folder (jar/jpackage are often not on PATH) ---
$props = cmd /c "java -XshowSettings:properties -version 2>&1"
$javaHomeLine = $props | Where-Object { $_ -match "^\s*java\.home\s*=" } | Select-Object -First 1
if (-not $javaHomeLine) { throw "Could not find Java. Is a JDK installed and 'java' on PATH?" }
$bin = Join-Path ($javaHomeLine -split "=", 2)[1].Trim() "bin"
foreach ($tool in "javac.exe", "jar.exe", "jpackage.exe") {
    if (-not (Test-Path (Join-Path $bin $tool))) { throw "$tool not found in $bin. Install a full JDK (not just a JRE)." }
}
Write-Host "Using JDK tools from $bin"

function Run($exe, [string[]]$argList) {
    & (Join-Path $bin $exe) @argList
    if ($LASTEXITCODE -ne 0) { throw "$exe failed (exit code $LASTEXITCODE)" }
}

# --- Clean old output ---
foreach ($dir in "out", "build", "dist") {
    if (Test-Path $dir) { Remove-Item $dir -Recurse -Force }
}

# --- 1. Compile (relative paths avoid problems with non-English folder names) ---
Write-Host "Compiling..."
$sources = Get-ChildItem -Recurse src -Filter *.java | Resolve-Path -Relative
Run "javac.exe" (@("--release", "17", "-d", "out") + $sources)

# --- 2. Package as a runnable jar ---
Write-Host "Creating jar..."
New-Item -ItemType Directory build | Out-Null
Run "jar.exe" @("--create", "--file", "build\SimpleFileExplorer.jar", "--main-class", "explorer.Main", "-C", "out", ".")

# --- 3. Turn the jar into a Windows app (Java runtime included) ---
Write-Host "Creating app (this takes a minute)..."
Run "jpackage.exe" @(
    "--type", "app-image",
    "--name", "SimpleFileExplorer",
    "--app-version", "1.0",
    "--input", "build",
    "--main-jar", "SimpleFileExplorer.jar",
    "--main-class", "explorer.Main",
    "--icon", "assets\app.ico",
    "--dest", "dist"
)

$exe = Resolve-Path "dist\SimpleFileExplorer\SimpleFileExplorer.exe"
Write-Host ""
Write-Host "Done! Your app is here:" -ForegroundColor Green
Write-Host "  $exe"
Write-Host "Right-click it to pin to Start / taskbar, or to create a desktop shortcut."
