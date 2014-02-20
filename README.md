filesystem-scanner
======================================================

Scans the specified paths and persists pairs of root path names and leaf file names.


-------------------------
| application structure |
-------------------------

bin:  contains three executables, one to scan desired paths (scanner.sh), one to archive the scanned content elsewhere so it isn't rescanned again next time (archiver.sh), and one that does both the previous jobs in cascade (scanner_and_archiver.sh)

config: contains database connection pooling configurations (c3p0.properties), log4j configurations (log4j.properties), and the main application configurations (config.properties)

lib:  contains the libraries required to compile and run this application

log:  contains the logs produced by the application

src:  contains the IDE-independant source code of the application

src/META-INF: contains Hibernate specific configuration files

