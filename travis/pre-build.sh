#!/bin/bash

mkdir temp

cd temp

wget --output-document=openfire_3_7_0.tar.gz http://www.igniterealtime.org/downloadServlet?filename=openfire/openfire_3_7_0.tar.gz

tar -xf openfire_3_7_0.tar.gz

unpack200 openfire/lib/openfire.jar.pack openfire.jar

mvn install:install-file --quiet -Dfile=openfire.jar -DgroupId=org.igniterealtime -DartifactId=openfire -Dversion=3.7.0 -Dpackaging=jar


cd ../

rm -rf temp