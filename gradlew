#!/usr/bin/env sh
set -eu

GRADLE_VERSION="9.5.1"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
GRADLE_BASE="$GRADLE_USER_HOME/wrapper/dists/mover"
GRADLE_HOME="$GRADLE_BASE/gradle-$GRADLE_VERSION"

if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
    mkdir -p "$GRADLE_BASE"
    ZIP="$GRADLE_BASE/gradle-$GRADLE_VERSION-bin.zip"
    URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"

    if command -v curl >/dev/null 2>&1; then
        curl --fail --location --retry 3 "$URL" --output "$ZIP"
    elif command -v wget >/dev/null 2>&1; then
        wget "$URL" --output-document="$ZIP"
    else
        echo "Error: curl or wget is required to download Gradle." >&2
        exit 1
    fi

    if command -v unzip >/dev/null 2>&1; then
        unzip -q -o "$ZIP" -d "$GRADLE_BASE"
    else
        echo "Error: unzip is required to extract Gradle." >&2
        exit 1
    fi
    rm -f "$ZIP"
fi

exec "$GRADLE_HOME/bin/gradle" "$@"
