#!/bin/bash

engine=${1}
test=${2}
loops=2000000
port=3306
host=192.168.1.50
MAINDIR=/opt/results
time=3600
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
  
	for threads in 1 32 64 96 128 256;do
		echo "RUNNING Test $test READ ONLY Thread=$threads [START] $(print_date_time) " >> "${LOGFILE}" 
		echo "======================================" >>  "${LOGFILE}"
		sysbench /opt/tools/sysbench/src/lua/windmills/oltp_read.lua  --mysql-host=192.168.1.50 --mysql-port=$port --mysql-user=m8_test --mysql-password=test --mysql-db=sbenchw --db-driver=mysql --tables=300 --table_size=20000000  --time=$time  --rand-type=zipfian --rand-zipfian-exp=0 --skip-trx=on  --report-interval=1 --mysql-ignore-errors=all  --auto_inc=off --histogram --table_name=windmills  --stats_format=csv --db-ps-mode=disable --threads=$threads run >> "${LOGFILE}"
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
  
	for threads in 1 32 64 96 128 256;do
		echo "RUNNING Test $test OLTP Thread=$threads [START] $(print_date_time) " >> "${LOGFILE}" 
		echo "======================================" >>  "${LOGFILE}"
		sysbench /opt/tools/sysbench/src/lua/windmills/oltp_read_write.lua  --mysql-host=192.168.1.50 --mysql-port=$port --mysql-user=m8_test --mysql-password=test --mysql-db=sbenchw --db-driver=mysql --tables=300 --table_size=20000000  --time=$time  --rand-type=zipfian --rand-zipfian-exp=0 --skip-trx=on  --report-interval=1 --mysql-ignore-errors=all  --auto_inc=off --histogram --table_name=windmills  --stats_format=csv --db-ps-mode=disable --threads=$threads run >> "${LOGFILE}"
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
		sysbench /opt/tools/sysbench-tpcc/tpcc.lua --mysql-host=192.168.1.50 --mysql-port=$port --mysql-user=m8_test --mysql-password=test --mysql-db=tpcc --db-driver=mysql --tables=10 --scale=100 --time=$time  --rand-type=zipfian --rand-zipfian-exp=0 --report-interval=1 --mysql-ignore-errors=all --histogram  --stats_format=csv --db-ps-mode=disable --threads=$threads run  >>  "${LOGFILE}"  
		echo "======================================" >>  "${LOGFILE}" 
		echo "RUNNING Test $test Thread=$threads [END] $(print_date_time) " >>  "${LOGFILE}" 
	done;
	echo "Testing  $test $(date +'%Y-%m-%d_%H_%M_%S') [END]" >>  "${LOGFILE}" ; 
    cd /opt/tools
fi