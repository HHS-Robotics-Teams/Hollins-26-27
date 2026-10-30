# Hollins 26-27 FTC robot code

FTC 2026-27 season (game: **BIOBUZZ**). Team robots share one codebase; each robot is chosen at
runtime by its Control Hub config name. Manual: `doc/BIOBUZZ_Competition_Manual_TU04.pdf`.
Team reference doc (configs, tuning results, field map):
https://claude.ai/code/artifact/e06bb36f-11e8-4ca1-8ced-bda90ed1090b

## Build and deploy
- Build from the command line with the JDK Android Studio uses:
  `JAVA_HOME=C:/Users/bruce/.jdks/jbr-21.0.11 ./gradlew assembleDebug`
  (`C:\Program Files\Android\Android Studio\jbr` is a broken leftover install; the real IDE is `Android Studio2`.)
- MeepMeep: `./gradlew :MeepMeepTesting:run`, or the run arrow on `main()` in `MeepMeepTesting.java`.
- Deploy with Android Studio Run over USB. Wi-Fi to the Control Hub cuts the laptop's internet.
- FTC Dashboard over USB, keeping internet: `adb forward tcp:8080 tcp:8080` and `adb forward tcp:8000 tcp:8000`,
  then http://localhost:8080/dash (adb is in `C:/Users/bruce/AppData/Local/Android/Sdk/platform-tools`).
  If Dashboard is off, run the Enable/Disable Dashboard OpMode once.

## Versions (read before changing build files)
- FTC SDK 12.0 (merged from FIRST's FtcRobotController; this repo shares its history, so later SDK updates are
  `git fetch https://github.com/FIRST-Tech-Challenge/FtcRobotController.git tag vX` + merge).
- Road Runner ftc 0.1.25, core/actions 1.0.1, FTC Dashboard 0.5.1, MeepMeep 0.1.7 (matches road-runner-quickstart).
- Gradle 9.6.0 with Android Gradle Plugin 9.4.1 (accepted from an Android Studio upgrade on 2026-10-27; builds OK).
  FIRST ships Gradle 9.1.0 / AGP 8.13.2. AGP 8.x does NOT work on Gradle 9.6+ (removed internal API), so never
  mix AGP 8 with newer Gradle. Expect conflicts in `build.gradle` / gradle-wrapper on the next SDK merge.
- SDK 12 AprilTags: detections are `AprilTagSingleDetection` or `AprilTagClusterDetection`; BIOBUZZ tags are
  4-tag clusters ("RED SCORING", "RED AUDIENCE", "BLUE AUDIENCE", "BLUE SCORING"). They move with the HIVE:
  good for aiming, not for field localization.

## Robot profiles (`TeamCode/.../robots/`)
- `RobotProfiles` matches the active config name's prefix (before the first `-`, case-insensitive) to a `Robot`.
  Unknown name = clear error at init. A `-comp` suffix (e.g. `nerdvana-comp`) hides test AUTOs.
- One folder per robot: `<Robot>.java` (drive `MecanumDrive.Params`: localizer, motor/encoder reverse flags,
  hub orientation, tuning), `<Tag>Route.java` (`RouteTuning`: size, intake sides, launcher; no hardware imports,
  shared with MeepMeep), `<Tag>Mechanisms.java` (launch/intake Actions), `<Tag>Autos.java` (approved AUTO list).
- Add a robot: copy a folder, rename classes with the new tag, add it to `RobotProfiles.ROBOTS`.
- Live tuning in Dashboard is under the robot's class (e.g. `TheCube.PARAMS`), not `MecanumDrive`.
- Device names are the same on every robot: `leftFront`, `leftBack`, `rightFront`, `rightBack`, `imu`,
  `pinpoint` (if fitted). camelCase, config name = Java variable name.

| Robot | Config | Odometry | Hub (logo/USB) | Status |
| --- | --- | --- | --- | --- |
| theCube (test bot) | `theCube` | drive encoders, goBILDA 5203 312 RPM direct | LEFT / UP | inPerTick 0.02284, lateralInPerTick 0.02678, trackWidthTicks 550, kS 1.62, kV 0.00406; next: ManualFeedforwardTuner (kA), ManualFeedbackTuner, SplineTest |
| nerdvana (15006) | `nerdvana` | Pinpoint, 2 goBILDA 4-bar pods | RIGHT / UP | untuned; right side reversed (unconfirmed) |

## Road Runner notes learned on this robot
- `RawEncoder` ignores the motor's direction, so `MecanumDrive` applies each reverse flag to both the motor
  and its encoder. Without that, drive-encoder x/y cancel to 0.
- Drive-encoder robots: ForwardRampLogger / LateralRampLogger refuse to run. Use ForwardPushTest,
  LateralPushTest, then AngularRampLogger (kS, kV, track width). Pinpoint robots use the dead-wheel sequence.
- AngularRampLogger: stop it once power stops climbing (~9 s); the full-power hold just rocks the robot.

## Field and AUTO (`TeamCode/.../field/`, `auto/`)
- Frame (`FieldPositions`): inches, origin at field center, viewed from the RED alliance station: red wall
  y = -72, blue wall y = +72, audience x = +72. Heading 0 faces the audience, CCW positive. Matches
  `MeepMeepTesting/field.png`. The manual's figures are drawn from the audience (rotated 90 degrees).
- Positions are written for red; blue = red rotated 180 degrees (`Alliance.pose()` / `point()`).
  Red half is y < 0. Measured from drawings (+/-1 in field tolerance): verify on a real field.
- Routes live in `AutoPaths` and are listed in the `Routes` enum (stage DRAFT = MeepMeep only, TEST = also on
  every robot's Driver Station as "~RED Hive Side [tag]"). Routes use `Approach` + `RouteTuning`, never
  hard-coded robot geometry, and call `Mechanisms` for launch/intake. `AutoOpModes` registers them;
  `RouteOpMode` runs one. Approved per-robot lists show as "RED <name>" / "BLUE <name>".
- MeepMeep compiles `field/**` and `robots/*/*Route.java` from TeamCode, so it runs the exact route code.
  Pick what to simulate at the top of `MeepMeepTesting.java`.
- Game rules that shape AUTO: 30 s AUTO; start on own half touching a wall, not in the LOADING ZONE or
  touching a FLOWER, contacting exactly 4 POLLEN (team plans 3 loaded + 1 on the tiles); LEAVE 3, PARK
  (partly in LOADING ZONE) 5, HIVE TIP 20.

## Conventions
- Match surrounding code style. Commit messages: imperative summary line, short body.
- `doc/angular-ramp-*.json` tuning logs are local data, not committed.
