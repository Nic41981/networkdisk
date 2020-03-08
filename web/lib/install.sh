#/user/bin/bash
mvn install:install-file -DgroupId=com.google.code -DartifactId=kaptcha -Dversion=2.3.2 -Dfile=./kaptcha-2.3.2.jar -Dpackaging=jar -DgeneratePom=true