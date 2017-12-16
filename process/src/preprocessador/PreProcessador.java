/* 
PRÉ-PROCESSADOR DE TWEETS
AUTOR: IGOR TANNUS CORREA
https://igoortc.github.io/
*/

package preprocessador;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class PreProcessador {
    ArrayList<String> tweets = new ArrayList<>();
    Map<String, String> dic = new HashMap<>();
    Map<String, String> emoticons = new HashMap<>();
    ArrayList<String> stopwords = new ArrayList<>();
    ArrayList<String> unrelated = new ArrayList<>();
    String[] lista_docs = {"arquivo_de_saida.csv"};

    public PreProcessador() {
        // escolha quais substituições deseja realizar
        geraListaDeStopwords();
        geraDicionarioGirias();
        geraDicionarioEmoticons();
        geraListaDeNaoRelacionadas();
        processarTexto();
    }

    public static void main(String[] args) {
        new PreProcessador();
    }

    private void processarTexto(){
        for(String s : lista_docs){
            try {    
                try (BufferedReader arq = new BufferedReader(new FileReader(s))) {
                    while(arq.ready()){
                        String linha = " "+arq.readLine()+" ";
                        linha = linha.toLowerCase();
                        s = s.replace( "#" , " ");
                        s = s.replace( "@" , " ");
                        if (!naoRelacionado(linha)) { // retirar condição caso não deseja retirar tweets não relacionados ao tema
                            for (Integer i = 0; i < 5; i++) {
                                linha = removerUrl(linha);
                            }
                            // escolha aqui quais etapas de pré-processamento deseja realizar
                            linha = removerUrlRegex(linha);
                            linha = removerUrlPicTwitter(linha);
                            linha = substituirEmoticons(linha);
                            linha = removerCaracteresEspeciaisEConsulta(linha);
                            linha = substituirLetrasRepetidas(linha);
                            linha = substituirGirias(linha);
                            linha = removerNumeros(linha);
                            linha = removerStopwords(linha);
                            linha = removerEspacos(linha);
                            if(linha.charAt(0)==' '){
                                linha = linha.substring(1);
                            }
                            if(linha.endsWith(" ")) {
                              linha = linha.substring(0,linha.length() - 1);
                            }
                            if (linha == null || linha.trim().isEmpty()) { }
                            else {
                                tweets.add(linha);
                            }
                        }
                    }
                }
            } catch(IOException e) {
                System.out.println("!!! TWEETS COLLECTION NOT OK\n\n");
                System.exit(0);
            }

            try{
                try (PrintWriter writer = new PrintWriter("NOVO_"+s, "UTF-8")) {
                    for (int i = 0; i < tweets.size(); i++) {
                        writer.println(tweets.get(i));
                    }
                    System.out.println("NOVO_"+s.toUpperCase()+" OK");
                    Toolkit.getDefaultToolkit().beep();
                }
            } catch (IOException e) {
                System.out.println("!!! NOVO_"+s.toUpperCase()+" NOT OK\n\n");
            }
        }
    }
    
    private void geraListaDeNaoRelacionadas(){
        try {    
            // unrelated.txt é o arquivo contendo palavras não relacionadas ao tema
            // tweets contendo no mínimo uma palavra da lista de termos são removidos
            try (BufferedReader arq = new BufferedReader(new FileReader("unrelated.txt"))) {  
                while(arq.ready()){
                    String linha = arq.readLine();
                    linha = linha.toLowerCase();
                    unrelated.add(linha);
                }
                System.out.println("GERAR NÃO RELACIONADAS OK\n\n");
            }
        } catch(IOException e) {
            System.out.println("!!! NÃO RELACIONADAS NOT OK\n\n");
            System.exit(0);
        }
    }
    
    public boolean naoRelacionado(String s){
        for (String key : unrelated) {
            if (s.matches(".*\\b"+key+"\\b.*")) {
                return true;
            }
        }

        return false;
    }

    private void geraDicionarioGirias(){
        try {
            // dictionary.txt é o arquivo contendo gírias/ abreviações que são substituídas por suas respectivas traduções
            try (BufferedReader arq = new BufferedReader(new FileReader("dictionary.txt"))) {
                while(arq.ready()){
                    String linha = arq.readLine();
                    linha = linha.toLowerCase();

                    String[] values = linha.split(",");
                    dic.put(values[0], values[1]);
                }
                System.out.println("GERAR GÍRIAS OK\n\n");
            }
        } catch(IOException e) {
            System.out.println("!!! GÍRIAS NOT OK\n\n");
            System.exit(0);
        }
    }

    public String substituirGirias(String s){
        for (String key : dic.keySet()) {
            if (s.matches(".*\\b"+key+"\\b.*")) {
                s = s.replaceAll("\\b"+key+"\\b", dic.get(key));
            }
        }

        return s;
    }

    private void geraListaDeStopwords(){
        try {
            // stopwords.txt é o arquivo contendo palavras stop words, que são removidas do tweet
            // fonte: http://www.lextek.com/manuals/onix/stopwords1.html
            try (BufferedReader arq = new BufferedReader(new FileReader("stopwords.txt"))) {
                while(arq.ready()){
                    String linha = arq.readLine();
                    linha = linha.toLowerCase();
                    stopwords.add(linha);
                }
                System.out.println("GERAR STOPWORDS OK\n\n");
            }
        } catch(IOException e) {
            System.out.println("!!! STOPWORDS NOT OK\n\n");
            System.exit(0);
        }
    }

    public String removerStopwords(String s){
        for (String key : stopwords) {
            if (s.matches(".*\\b"+key+"\\b.*")) {
                s = s.replaceAll("\\b"+key+"\\b", " ");
            }
        }

        return s;
    }

    private void geraDicionarioEmoticons(){
        try {
            // emoticons.txt é o arquivo contendo emoticons que são substituídoss por suas respectivas traduções
            try (BufferedReader arq = new BufferedReader(new FileReader("emoticons.txt"))) {
                while(arq.ready()){
                    String linha = arq.readLine();
                    linha = linha.toLowerCase();

                    String[] values = linha.split(",");
                    emoticons.put(values[0], values[1]);
                }
                System.out.println("GERAR EMOTICONS OK\n\n");
            }
        } catch(IOException e) {
            System.out.println("!!! EMOTICONS NOT OK\n\n");
            System.exit(0);
        }
    }


    public String substituirEmoticons(String s){
        for (String key : emoticons.keySet()) {
            if (s.contains(" "+key+" ")) {
                s = s.replace(" "+key+" ", " "+emoticons.get(key)+" ");
            }
        }
        return s;
    } 

    public static String removerUrl(String s){
        String aux ="";
        if (s.contains("http")) {
            if (s.contains("…")) {
                Integer http = s.indexOf("http");
                if (s.indexOf("…", http) != -1) {
                    aux = s.substring(s.indexOf("http"), s.indexOf("…", http)+1);
                    s = s.replace(aux, " ");
                }
            }
        }
        s = s.replaceAll("@\\s*(\\w+)", " ");
        return s;
    }

    public static String removerUrlRegex(String s){
        s = s.replaceAll("https?://.*?\\s+", "INICIO_LINK");
        if (s.contains("INICIO_LINK")) {
            String aux = s.substring(s.indexOf("INICIO_LINK"));
            s = s.replace(aux, " ");
        }
        return s;
    }

    public static String removerUrlPicTwitter(String s){
        if (s.contains("pic.twitter.com")) {
            String aux = s.substring(s.indexOf("pic.twitter.com"));
            s = s.replace(aux, " ");
        }
        return s;
    }

    public static String removerCaracteresEspeciaisEConsulta(String s) {
        s = s.replaceAll("[^\\p{ASCII}]", "");
        // substitui os seguintes sinais de pontuação por espaço
        s = s.replace( "." , " ");
        s = s.replace( "/" , " ");
        s = s.replace( "-" , " ");
        s = s.replace( ";" , " ");
        s = s.replace( "!" , " ");
        s = s.replace( ":" , " ");
        s = s.replace( "?" , " ");
        s = s.replace( "," , " ");
        s = s.replace( "(" , " ");
        s = s.replace( ")" , " ");
        s = s.replace( "[" , " ");
        s = s.replace( "]" , " ");
        s = s.replace( "\"" , " ");
        s = s.replace( "'" , "");
        s = s.replace( "#" , " ");
        s = s.replace( "&", " ");
        s = s.replace( "=", " ");
        s = s.replace( "|", " ");
        s = s.replace( ">", " ");
        s = s.replace( "<", " ");
        s = s.replace( "*", " ");
        s = s.replace( "_", " ");
        s = s.replace( "%", " ");
        s = s.replace( "@", " ");
        s = s.replace( "\\", " ");
        s = s.replace( "{", " ");
        s = s.replace( "}", " ");
        s = s.replace( "^", " ");
        s = s.replace( "$", " ");
        s = s.replace( "+", " ");
        s = s.replace( "*", " ");
        s = s.replace( "~", " ");
        s = s.replace( "`", " ");
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        // incluir os nomes das consultas -- ex. títulos dos filmes -- caso queira retirar esses termos
        s = s.replace("arrival", " ");
        s = s.replace("fences", " ");
        s = s.replace("hacksaw ridge", " ");
        s = s.replace("hacksawridge", " ");
        s = s.replace("hacksaw", " ");
        s = s.replace("hell or high water", " ");
        s = s.replace("hell high water", " ");
        s = s.replace("hellorhighwater", " ");
        s = s.replace("hidden figures", " ");
        s = s.replace("hiddenfigures", " ");
        s = s.replace("hidden figure", " ");
        s = s.replace("hiddenfences", " ");
        s = s.replace("hidden fences", " ");
        s = s.replace("la la land", " ");
        s = s.replace("lalaland", " ");
        s = s.replace("lala", " ");
        s = s.replace("lion", " ");
        s = s.replace("manchester by the sea", " ");
        s = s.replace("manchester sea", " ");
        s = s.replace("manchester", " ");
        s = s.replace("manchesterbythesea", " ");
        s = s.replace("moonlight", " ");

        return s;
    }

    public static String removerTermoConsulta(String s, String consulta) {
        s = s.replace(consulta, " ");

        return s;
    }

    public static String removerNumeros(String s) {
        s = s.replaceAll("[0-9]", " ");

        return s;
    }

    public static String substituirLetrasRepetidas(String s) {
        s = s.replaceAll ("(\\w)\\1{2,}", "$1"); //substitui letras repetidas
        // s = s.replaceAll ("(([A-Za-z])(\\2{2})+)", "$2"); //substitui letras repetidas

        return s;
    }

    public static String removerEspacos(String s) {
        s = s.replaceAll("\\s{2,}", " "); // normaliza os espaços
        return s;
    }
}