#! /bin/bash
# -----------------------------------------------------------------------------
# ____   ____  ____    ____  _______      ______  
#|_  _| |_  _||_   \  /   _||_   __ \   .' ___  | 
#  \ \   / /    |   \/   |    | |__) | / .'   \_| 
#   \ \ / /     | |\  /| |    |  __ /  | |        
#    \ ' /     _| |_\/_| |_  _| |  \ \_\ `.___.'\ 
#     \_/     |_____||_____||____| |___|`.____ .'
#
# Script to execute the VMRC client operations.
#
# -----------------------------------------------------------------------------


command_exists () {
    type "$1" &> /dev/null ;
}

if command_exists java; then
  JAVAPATH=java
else
  if [ -z $JAVA_HOME ]; then
    echo "ERROR: environment variable JAVA_HOME not defined"  1>&2
    exit -1
  else
    JAVAPATH="$JAVA_HOME"/bin/java
  fi
fi

if [ -z $VMRC_CLIENT_LOCATION ]; then
   echo "ERROR: The environment variable VMRC_CLIENT_LOCATION is not defined" 1>&2
   exit -1
fi

CLASSPATH=$CLASSPATH:$VMRC_CLIENT_LOCATION/vmrc-client.jar
for i in ${VMRC_CLIENT_LOCATION}/lib/*.jar
do
  CLASSPATH=$CLASSPATH:"$i"
done

#echo "CLASSPATH is $CLASSPATH"

PRG="org.grycap.vmrc.client.cmd.VMRCCLI"

# Execute VMRC client.
exec $JAVAPATH -classpath $CLASSPATH $PRG $@
