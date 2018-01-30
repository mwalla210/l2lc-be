import paramiko
import os
import traceback
import sys
import socket
from paramiko.py3compat import u
from multiprocessing import Process


port = 22

configFileName = "config"
transferFrom = 'l2lc-db'
transferTo = '~'

UseGSSAPI = True             # enable GSS-API / SSPI authentication
DoGSSAPIKeyExchange = True

paramiko.util.log_to_file('demo.log')

def main():
    config = readConfigs(configFileName)
    username = config['username']
    password = config['password']
    host = config['host']
    client = connect(username, password, host, port)
    stdin_, stdout_, stderr = client.exec_command("mvn -f ~/l2lc-db/pom.xml clean tomcat7:undeploy")
    readFromHost(stdout_)
    stdin_, stdout_, stderr = client.exec_command("git -C ~/l2lc-db pull")
    readFromHost(stdout_)
    stdin_, stdout_, stder =  client.exec_command("mvn -f ~/l2lc-db/pom.xml clean tomcat7:deploy")
    readFromHost(stdout_)

    client.close()
def readFromHost(stdout_):
    stdout_.channel.recv_exit_status()
    lines = stdout_.readlines()
    for line in lines:
        print line


def connect(username, password, host, port):
    try:
        client = paramiko.SSHClient()
        client.load_system_host_keys()
        client.set_missing_host_key_policy(paramiko.WarningPolicy())
        print('*** Connecting...')
        client.connect(host, port, username, password)
        return client
    except Exception as e:
        print('*** Caught exception: %s: %s' % (e.__class__, e))
        traceback.print_exc()
        try:
            client.close()
        except:
            pass
        sys.exit(1)

def readConfigs(filename):
    file = open(filename, "r")
    d = dict()
    for line in file:
        k, a = line.split('=')
        k = k.strip()
        a = a.strip()
        d[k] = a
    return d

if __name__ == "__main__":
    main()
