#!/usr/bin/env bash
# Build (and optionally install) the MEET build: the robot app without any UNO Q code.
#
#   tools/meet-build.sh            # compile + build the meet APK, check no UNO Q code is in it
#   tools/meet-build.sh --install  # ...then install it on the Control Hub over USB (adb)
#
# FTC does not allow co-processors on the robot, so meet builds leave out teamcode/unoq/
# (TeamCode/build.gradle, -PmeetBuild). Main code must never import from unoq/: if it does, this
# build fails to compile, which is the point. Run this before every meet.
#
# Android Studio's Run button always installs the PRACTICE build (with unoq/). For a meet, use
# this script with --install, or keep the Control Hub on the APK this script installed.
set -euo pipefail
cd "$(dirname "$0")/.."

export JAVA_HOME="${JAVA_HOME:-C:/Users/bruce/.jdks/jbr-21.0.11}"
ADB="${ADB:-C:/Users/bruce/AppData/Local/Android/Sdk/platform-tools/adb}"
INSTALL=0
[[ "${1:-}" == "--install" ]] && INSTALL=1

CLASSES=TeamCode/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes/org/firstinspires/ftc/teamcode

echo "== Meet build (no teamcode/unoq/) =="
# Remove old compiled classes so a stale unoq/ class can't hide in the output.
rm -rf "$CLASSES/unoq"
./gradlew :TeamCode:assembleDebug -PmeetBuild --console=plain -q

if [[ -d "$CLASSES/unoq" ]]; then
  echo "!! UNO Q classes were compiled into the meet build: check the meetBuild exclude in TeamCode/build.gradle" >&2
  exit 1
fi
APK=$(ls -t TeamCode/build/outputs/apk/debug/*.apk | head -1)

# Second check, on the APK itself: no class from teamcode/unoq/ in any dex file.
if command -v python >/dev/null 2>&1; then
  refs=$(python - "$APK" <<'EOF'
import sys, zipfile
z = zipfile.ZipFile(sys.argv[1])
print(sum(z.read(f).count(b'teamcode/unoq/') for f in z.namelist() if f.endswith('.dex')))
EOF
)
  if [[ "$refs" != "0" ]]; then
    echo "!! $APK still contains UNO Q classes ($refs references); not a meet build" >&2
    exit 1
  fi
fi
echo "OK: meet build compiled without teamcode/unoq/  ->  $APK"

if (( INSTALL )); then
  echo "== Installing on the Control Hub (USB) =="
  "$ADB" install -r "$APK"
  echo "Installed the MEET build. Android Studio Run will put the practice build back."
fi
