package com.demo.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.demo.domain.GitClone;
import com.demo.domain.MediumRoom;
import com.demo.domain.PMDErrors;
import com.demo.repositories.PMDErrorsRepository;
import com.demo.security.UserSS;
import com.demo.services.MediumRoomService;
import com.demo.services.PMDErrorService;
import com.opencsv.CSVReader;;

@Component
public class ReceiverPMD {
	private GitClone gitClone;
	private String dirGit;

	@Autowired
	private PMDErrorService pmdErrorService;
	
	@Autowired
	private MediumRoomService mediumRoomService;

	@JmsListener(destination = "pmdAnalyser", containerFactory = "myFactory")
	public void pmdAnalyser(GitClone gitClone){
		this.setGitClone(gitClone);
		
		mediumRoomService.setInExecutionPMDStatus(this.getGitClone());
		
		System.out.println("Analizando REPOSITORIO <" + gitClone.getRoom().getGit() + ">");

		/** Obtem o diretório onde está o codigo git */
		this.setDirGit("./repsgit/" + gitClone.getUserid() + "/" + gitClone.getRoom().getId());

		//this.pmdReportJavaBasic();
		//this.pmdReportCodesize();
		this.pmdReportDesign();
        		
		mediumRoomService.setCompletePMDStatus(this.getGitClone());
		
	}

	private void pmdReportJavaBasic() {
		makeReport("rulesets/java/basic.xml", "basic");
		databaseImportCSV("basic");
	}

	private void pmdReportCodesize() {
		makeReport("rulesets/java/codesize.xml", "codesize");
		databaseImportCSV("codisize");
	}

	private void pmdReportDesign() {
		makeReport("category/java/design.xml", "design");
		databaseImportCSV("design");
	}

	private void pmdReportNaming() {
		makeReport("rulesets/java/naming.xml", "naming");
		databaseImportCSV("naming");
	}

	private void makeReport(String roleName, String reportName) {
		String[] commands = { "/home/lucas/pmd-bin-6.11.0/bin/run.sh", "pmd", "-d", this.getDirGit(), "-f",
				"csv", "-R", roleName, "-version", "1.7", "-language", "java", "-r",
				dirGit + "/" + reportName + "_report.csv" };
		Process proc;
		try {
			Runtime rt = Runtime.getRuntime();
			proc = rt.exec(commands);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				System.out.println("padrao:" + s);
			}
			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
   
	private void databaseImportCSV(String report) {
		try {
			CSVReader reader = new CSVReader(new FileReader(this.getDirGit() + "/" + report + "_report.csv"));

			String[] nextLine;
			int lineNumber = 0;
			while ((nextLine = reader.readNext()) != null) {
				if (lineNumber > 0) {
				 if( nextLine[7].equals("ExcessiveMethodLength")  
				    || nextLine[7].equals("GodClass")
				    || nextLine[7].equals("DataClass")
				    || nextLine[7].equals("ExcessiveParameterList")
				    || nextLine[7].equals("TooManyMethods")
				  ) {
					PMDErrors pmdErrors = new PMDErrors();
					pmdErrors.setJpackage(nextLine[1]);
					pmdErrors.setFile_dir(nextLine[2]);
					pmdErrors.setPriority(Integer.parseInt(nextLine[3]));
					pmdErrors.setLine(Integer.parseInt(nextLine[4]));
					pmdErrors.setDescription(nextLine[5]);
					pmdErrors.setRule(nextLine[6]);
					pmdErrors.setRule_set(nextLine[7]);
					pmdErrors.setRoom(this.getGitClone().getRoom());
					pmdErrorService.save(pmdErrors);
				 }
				}else{
					lineNumber++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public GitClone getGitClone() {
		return gitClone;
	}

	public void setGitClone(GitClone gitClone) {
		this.gitClone = gitClone;
	}

	public String getDirGit() {
		return dirGit;
	}

	public void setDirGit(String dirGit) {
		this.dirGit = dirGit;
	}
	
	
}
