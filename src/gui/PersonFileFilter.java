package gui;


import java.io.File;
import javax.swing.filechooser.FileFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Andreas
 */
public class PersonFileFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        
        if(file.isDirectory()){
            return true;
        }
        
        String name = file.getName();
        
        String extension = Utils.getFileExtension(name);
        
        if(extension == null){
            return false;
        }
        
        if(extension.equals("per")){
            return true;
        }
        
        return false;
    }

    @Override
    public String getDescription() {
        return "Person database files (*.per)";
    }
  }
