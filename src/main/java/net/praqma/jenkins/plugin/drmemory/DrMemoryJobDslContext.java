package net.praqma.jenkins.plugin.drmemory;

import javaposse.jobdsl.dsl.Context;

class DrMemoryJobDslContext implements Context{
    String executable = "out.exe";
    
    public void executable (String value){
        executable = value;
    }
    
    String args = null;
    
    public void args(String value){
        args = value;
    }
    
    String logPath = "drmemory";
    
    public void logPath(String value){
        logPath = value;
    }
    
    boolean failedAsUnstable = false;
    
    public void failedAsUnstable(){
        failedAsUnstable = true;
    }
    
    public void failedAsUnstable(boolean value){
        failedAsUnstable = value;
    }
}
