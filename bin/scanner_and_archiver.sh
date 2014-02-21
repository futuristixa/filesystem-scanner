#!/bin/bash

dir=`dirname $0`
FILE_PATH=`cd  $dir;pwd`
MON_HOME=${FILE_PATH%/*}
BIN_PATH=${MON_HOME}/bin

ARCH_PATH=$1
SCAN_PATHS=${2}

print_usage()
{
	echo "The path of the archive folder must be specified as the first command line input of this shell"
	echo "The paths to scan must be specified as the second command line input of this shell"
	echo "To specify more than one path, they must be enclosed in double quotes"
}

if [ $# -lt 2 ]; then
	echo "Less than two input parameters found"
	print_usage
	exit
elif [ $# -ge 3 ]; then
	echo "Three or more input parameters found"
	print_usage
	exit
fi

$BIN_PATH/scanner.sh
$BIN_PATH/archiver.sh ${ARCH_PATH} ${SCAN_PATHS}
