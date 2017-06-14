echo “Running stressTool with additional parameters from command line  $2”
java -Xms2G -Xmx3G -classpath "./*:./lib/*" net.tc.stresstool.StressTool --defaults-file=$1 $2

