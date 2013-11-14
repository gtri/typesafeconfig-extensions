#!/bin/bash

VERSION="0.13.0"
JAR="project/sbt-launch-$VERSION.jar"
URL="http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/$VERSION/sbt-launch.jar"

if [ ! -f "$JAR" ]; then
  wget "$URL" -O "$JAR"
fi

SBT_OPTS="\
-Xms512M \
-Xmx1536M \
-Xss1M \
-XX:+CMSClassUnloadingEnabled \
-XX:PermSize=256M \
-XX:MaxPermSize=512M \
"

java $SBT_OPTS -jar "$JAR" "$@"
