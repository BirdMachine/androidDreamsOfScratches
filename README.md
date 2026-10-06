# androidDreamsOfScratches

**How far can we mutate an interface while it remains an interface?**

An Android UI/effects laboratory for functional, touchable, gloriously unreasonable interfaces. Kotlin + Jetpack Compose + AGSL RuntimeShader.

## v0.1 specimens
- Liquid gradient / domain warp
- Metaball goo
- Plasma interference field
- Aurora/noise
- Ripple pond
- Lens/refraction distortion
- Reaction–diffusion-inspired organic field
- SDF morphing controls

Every specimen keeps real UI on top: sliders, switches, buttons, text input, and touch interaction. The lab includes a shared uniform bench and a Normal UI torture test.

## Build
Android Studio: open the repository and run `app` on Android 13+ (API 33+).

CLI: `./gradlew assembleDebug`

GitHub Actions builds a debug APK on every push and uploads it as an artifact.

> Scratch is deliberately inconsistent. Visual consistency would make the laboratory less useful.
