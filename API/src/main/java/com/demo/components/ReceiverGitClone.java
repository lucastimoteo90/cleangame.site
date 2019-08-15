package com.demo.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.demo.domain.GitClone;
import com.demo.domain.MediumRoom;
import com.demo.security.UserSS;
import com.demo.services.MediumRoomService;
import com.demo.services.UserService;

@Component
public class ReceiverGitClone {
	@Autowired
	private MediumRoomService mediumRoomService;
	
	@JmsListener(destination = "mailbox", containerFactory = "myFactory")
    public void receiveMessage(String email) {
        System.out.println("Received <" + email + ">");
    }
	
	@JmsListener(destination = "gitClone", containerFactory = "myFactory")
	public void gitClone(GitClone gitClone) {
        System.out.println("Clonando REPOSITORIO <" + gitClone.getRoom().getGit() + ">");
        mediumRoomService.setInExecutionCloneStatus(gitClone);;
        /** Cria diretorio */
		// Artificial delay of 1s for demonstration purposes
		String dirGit = "./repsgit/" + gitClone.getUserid() + "/" + gitClone.getRoom().getId();

		File file = new File(dirGit);
		file.mkdir();

		Runtime rt = Runtime.getRuntime();
		String[] commands = {"git", "clone", gitClone.getRoom().getGit(), dirGit};
		Process proc;
		try {
			proc = rt.exec(commands);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				//TRATAR RETORNO
			}
			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				//TRATAR RETORNO		
			}
			
			System.out.println("AQUI:\n");
			mediumRoomService.setCompleteCloneStatus(gitClone);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			mediumRoomService.setCompleteCloneStatusError(gitClone.getRoom());
			e.printStackTrace();
		}
		        
	}
	
	
}
