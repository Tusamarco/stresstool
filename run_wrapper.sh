#!/bin/bash

engine=${1}
test=${2}
loops=2000000
port=3306
host=192.168.1.50
MAINDIR=/opt/results
time=3600
UseHardStop=false

LOGFILE=$MAINDIR/stresstool/stresstool_${test}_${engine}_$(date +'%Y-%m-%d_%H_%M').txt

echo "Running test:$test" 

print_date_time(){
 echo "$(date +'%Y-%m-%d_%H_%M_%S')"
}


nc -w 1 -z $host $port
if [ $? -ne 0 ] ; then
    echo "[ERROR] Mysql did not start correctly" >> "${LOGFILE}" 
  exit 1
else
  echo "[OK] Mysql running correctly" >> "${LOGFILE}" 
fi

	


if [ $test == "ingest" ] ;
 then 
	echo "     Testing  $test $(print_date_time) [START]" >> "${LOGFILE}" 
    cd /opt/tools/sysbench
    
	if [ ! -d "$MAINDIR/sysbench_results" ]; then
	    mkdir -p $MAINDIR/sysbench_results
	fi
  
	for threads in 2 8 16 32 64 128;do
		echo "RUNNING Test $test READ ONLY Thread=$threads [START] $(print_date_time) " >> "${LOGFILE}" 
		echo "======================================" >>  "${LOGFILE}"
		./run.sh ingest.cnf "main@connUrl=jdbc:mysql://${host}:${port},main@droptable=false, main@createTable=false, main@pctInsert=100, main@pctUpdate=0, main@pctDelete=0, main@pctSelect=0,main@UseHardStop=${UseHardStop}, main@HardStopLimit=${time},net.tc.stresstool.actions.DeleteBase@deleteRange=1, net.tc.stresstool.actions.DeleteBase@batchSize=1, net.tc.stresstool.actions.InsertBase@batchSize=20, connectionPool=false, stickyconnection=true, useConnectionPool=false,main@interactive=0,main@poolNumber=${threads},main@repeatNumber=$loops" >> "${LOGFILE}"
		echo "======================================" >> "${LOGFILE}" 
		echo "RUNNING Test $test Thread=$threads [END] $(print_date_time) " >> "${LOGFILE}"
	done;
    cd /opt/tools
	echo "Testing  $test $(date +'%Y-%m-%d_%H_%M_%S') [END]" >> "${LOGFILE}";
fi


if [ $test == "tpcc" ] ;
 then 
	echo "     Testing  $test $(print_date_time) [START]" >> "${LOGFILE}" 
    cd /opt/tools/sysbench
    
	if [ ! -d "$MAINDIR/sysbench_results" ]; then
	    mkdir -p $MAINDIR/sysbench_results
	fi
  
	for threads in 2 8 16 32 64 128;do
		echo "RUNNING Test $test OLTP Thread=$threads [START] $(print_date_time) " >> "${LOGFILE}" 
		echo "======================================" >>  "${LOGFILE}"
		./run.sh tpcc.cnf "main@connUrl=jdbc:mysql://${host}:${port},main@droptable=false, main@createTable=false, main@pctInsert=100, main@pctUpdate=50, main@pctDelete=50, main@pctSelect=0,main@UseHardStop=false,  net.tc.stresstool.actions.DeleteBase@deleteRange=5, net.tc.stresstool.actions.DeleteBase@batchSize=10, net.tc.stresstool.actions.InsertBase@batchSize=20, connectionPool=false, stickyconnection=true, useConnectionPool=false,main@interactive=0,main@poolNumber=${threads},main@repeatNumber=${loops}" >> "${LOGFILE}"
		echo "======================================" >> "${LOGFILE}" 
		echo "RUNNING Test $test Thread=$threads [END] $(print_date_time) " >> "${LOGFILE}"
	done;
    cd /opt/tools
	echo "Testing  $test $(date +'%Y-%m-%d_%H_%M_%S') [END]" >> "${LOGFILE}";
fi

if [ $test == "windmills" ] ;
 then 
    cd /opt/tools/sysbench-tpcc
	echo "     Testing  $test $(print_date_time) [START]">> "${LOGFILE}" 
	if [ ! -d "$MAINDIR/sysbench_results" ]; then
	    mkdir -p $MAINDIR/sysbench_results
	fi
  
	for threads in 1 32 64 96 128 256;do
		echo "RUNNING Test $test Thread=$threads [START] $(print_date_time) " >>  "${LOGFILE}" 
		echo "======================================" >>  "${LOGFILE}" 
		./run.sh windmills_mysql.cnf "main@connUrl=jdbc:mysql://${host}:${port},main@droptable=false, main@createTable=false, main@pctInsert=100, main@pctUpdate=50, main@pctDelete=50, main@pctSelect=0,main@UseHardStop=false,  net.tc.stresstool.actions.DeleteBase@deleteRange=5, net.tc.stresstool.actions.DeleteBase@batchSize=10, net.tc.stresstool.actions.InsertBase@batchSize=20, connectionPool=false, stickyconnection=true, useConnectionPool=false,main@interactive=0,main@poolNumber=${threads},main@repeatNumber=${loops}" >> "${LOGFILE}"
		echo "======================================" >>  "${LOGFILE}" 
		echo "RUNNING Test $test Thread=$threads [END] $(print_date_time) " >>  "${LOGFILE}" 
	done;
	echo "Testing  $test $(date +'%Y-%m-%d_%H_%M_%S') [END]" >>  "${LOGFILE}" ; 
    cd /opt/tools
fi



# ./run.sh windmills_mysql.cnf "main@connUrl=jdbc:mysql://192.168.4.55:3306,main@droptable=true,main@createTable=true,main@pctInsert=100,main@pctUpdate=0,main@pctDelete=0,main@pctSelect=0,main@UseHardStop=false,main@HardStopLimit=30,net.tc.stresstool.actions.DeleteBase@deleteRange=1,net.tc.stresstool.actions.DeleteBase@batchSize=1,net.tc.stresstool.actions.InsertBase@batchSize=20,connectionPool=false,stickyconnection=true,useConnectionPool=false,main@interactive=2,main@poolNumber=2,main@repeatNumber=50"