#!/bin/bash
BASEDIR=`dirname "$0"`
cd "$BASEDIR"
# use the following command for help:
#java at.jku.risc.stout.urau.AntiUnifyMain -h
java -Xmx2048m -XX:MaxPermSize=1024m -Xss128m at.jku.risc.stout.urau.AntiUnifyMain -m 1 -a "( f(a,b,c), g(a) ) =^= ( f(a,b,a,c), g(a), h(b) )"
read -p "Press [Enter] to exit"

