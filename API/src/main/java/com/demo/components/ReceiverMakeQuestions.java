package com.demo.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.demo.domain.GitClone;
import com.demo.domain.MediumRoom;
import com.demo.domain.PMDErrors;
import com.demo.domain.Question;
import com.demo.repositories.PMDErrorsRepository;
import com.demo.security.UserSS;
import com.demo.services.MediumRoomService;
import com.demo.services.PMDErrorService;
import com.demo.services.QuestionService;
import com.opencsv.CSVReader;;

@Component
public class ReceiverMakeQuestions {
	private GitClone gitClone;
	private String dirGit;
    
	
	
		
	@Autowired
	private MediumRoomService mediumRoomService;

	@Autowired
	private QuestionService questionService;
	
	@JmsListener(destination = "makeQuestions", containerFactory = "myFactory")
	public void mainMakeQuestions(GitClone gitClone) {
		this.setGitClone(gitClone);
		mediumRoomService.setInExecutionMakeQuestionStatus(this.getGitClone());
		
		System.out.println("GERANDO QUESTÕES <" + gitClone.getRoom().getGit() + ">");
		/*Refatorar...*/
		this.getGitClone().setRoom(mediumRoomService.findById(this.getGitClone().getRoom().getId()));
		//this.getGitClone().setRoom(mediumRoomService.findById(48));
		System.out.println("TOTAL DE ERROS NO PROJETO:" + this.getGitClone().getRoom().getPmdErrors().size());
    	makeQuestions();		
		mediumRoomService.setCompleteMakeQuestionStatus(this.getGitClone());
		
	}
	
	private void makeQuestions(){
		//Percorre todos os erros encontrados pelo pmd
		System.out.println("PERCORRER ERROS<" + gitClone.getRoom().getGit() + ">");
		int x = 0;
		for(PMDErrors pmderror : this.getGitClone().getRoom().getPmdErrorsUniqueErrorInFile()) {
			Question question = new Question();
			question.setCode(readCode(pmderror.getFile_dir()));
			question.setAsk("Identifique o Bad Smell na classe abaixo");
			
			if(pmderror.getRule_set().equals("DataClass")){
				question.setCorrect("Data Class");
				question.setFake1("Large Class");
				question.setFake2("Too Many Methods");
				question.setFake3("Long Method");
				
				question.setTip("Refere-se a uma classe que contém apenas campos e métodos brutos para acessá-los. Essas classes não contêm nenhuma funcionalidade adicional e não podem operar de forma independente nos dados que possuem.");
				question.setTip2("MÉTRICAS: WMC, WOC, NOAM, NOPA");
				question.setTip3("Próximo a linha: "+pmderror.getLine());
				
			}else if(pmderror.getRule_set().equals("GodClass")) {
				question.setCorrect("Large Class");
				question.setFake1("Data Class");
				question.setFake2("Too Many Methods");
				question.setFake3("Long Parameter List:");
				
				question.setTip("Refere-se a uma classe contém muitos campos / métodos / linhas de código.");
				question.setTip2("MÉTRICA: WMC,FEW,TCC,ATFD");
				question.setTip3("Próximo a linha: "+pmderror.getLine());
				
			}else if(pmderror.getRule_set().equals("ExcessiveMethodLength")) {
				question.setCorrect("Long Method");
				question.setFake1("Large Class");
				question.setFake2("Too Many Methods");
				question.setFake3("Long Parameter List:");

				question.setTip("Refere-se a um método longo, isso geralmente indica que o método está fazendo mais do que seu nome/assinatura poderia sugerir.");
				question.setTip2("MÉTRICA: Verifica se um método possiu mais de X linhas.");
				question.setTip3("Próximo a linha: "+pmderror.getLine());
			}else if(pmderror.getRule_set().equals("ExcessiveParameterList")) {
				
				question.setCorrect("Long Parameter List");
				question.setFake1("Large Class");
				question.setFake2("Too Many Methods");
				question.setFake3("Long Method");

				question.setTip("Refere-se a um método com vários parâmetros.");
				question.setTip2("MÉTRICA: Detecta uma lista de parâmetros anormalmente longa.");
				question.setTip3("Próximo a linha: "+pmderror.getLine());
			}else if(pmderror.getRule_set().equals("TooManyMethods")) {
				
				question.setCorrect("Too Many Methods");
				question.setFake1("Large Class");
				question.setFake2("Long Method");
				question.setFake3("Long Parameter List");

				question.setTip("Refere-se a uma classe com muitos métodos, que tende a agregar muitas responsabilidades e inevitavelmente se torna mais difícil de entender e, portanto, de manter.");
				question.setTip2("MÉTRICAS: NOM.");
				question.setTip3("Próximo a linha: "+pmderror.getLine());
			}
						
			
			question.setFilename(pmderror.getFile_dir().substring(48, pmderror.getFile_dir().length()));
						
			
			/*gera md5*/
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
				md.update(question.getCorrect().getBytes(),0,question.getCorrect().length());
				// System.out.println("MD5: "+new BigInteger(1,md.digest()).toString(16));
				
				question.setMd5correct(new BigInteger(1,md.digest()).toString(16));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			
			//question.setMd5correct(pmderror.getDescription());
			
			
			question.setRoom(this.getGitClone().getRoom());
			
			questionService.insert(question);
			System.out.println("PERCORRER ERROS<" + gitClone.getRoom().getGit() + ">");
			
			if(x++ > 100)
				 break;
			
		}
	}
	
	
	
	private String readCode(String file){
		String fileTxt = "";
	
		System.out.println("LER ARQUIVO <" + gitClone.getRoom().getGit() + ">");
		
		try {
			 FileReader arq = new FileReader(file);
		
			 BufferedReader lerArq = new BufferedReader(arq);
			 String linha = lerArq.readLine(); // lê a primeira linha
		      // a variável "linha" recebe o valor "null" quando o processo
		      // de repetição atingir o final do arquivo texto
			 fileTxt = linha;
			 while (linha != null) {
		        linha = lerArq.readLine(); // lê da segunda até a última linha
		        fileTxt =  fileTxt + linha+"\n";
		     }
		     arq.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fileTxt;
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
