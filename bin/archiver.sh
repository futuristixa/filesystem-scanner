#!/bin/bash

ARCH_PATH=$1
SCAN_PATHS=$2

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

for p in $SCAN_PATHS
do
        echo "The current path to scan is $p"
        cd $p
        TAR=`ls -l | awk '{print $9}'`
        echo "The list of files to tar and compress inside the current path ($p) is: $TAR"
        for q in $TAR
        do
        	echo "Analyzing $q"
                tar -cfv ${q}.tar $q
                echo "$q has been tar_ed"
                compress ${q}.tar
                echo "$q has been compressed"
                rm -Rf $q
                echo "$q has been removed"
        done
        echo "All the files in $p have been tar_ed, compressed and deleted"
       	echo "Moving all tars and compressed files in $p to $ARCH_PATH"
        mv $p/* $ARCH_PATH
        echo "All .tar.Z files have been moved to $ARCH_PATH"
done
