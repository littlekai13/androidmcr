#!/bin/bash

joone=$(ls `pwd`/joone-engine-2.0.0RC1/joone-engine.jar)
jcommon=$(ls `pwd`/jcommon-1.0.5/jcommon-1.0.5.jar)
jfreechart=$(ls `pwd`/jfreechart-1.0.1/lib/jfreechart-1.0.1.jar)

cd OpenOMR

classpath=$joone:$jcommon:.:$jfreechart

java -classpath $classpath -Xmx256m openomr.openomr.SheetMusic