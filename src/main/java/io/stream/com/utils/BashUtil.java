package io.stream.com.utils;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BashUtil {

    private static final String BASH_SCRIPTS_PATH = "src/main/resources/bash/";

    private static final String AUDIO_EXTRACTOR_BASH_FILENAME = "audio_extractor";

    private static final String MEDIA_CONVERTER_BASH_FILENAME = "converter";

    public static ProcessBuilder processBuilder(VideoProcessType processType, String... args){
        String[] commands = new String[args.length + 2];
        int i = 0;

        commands[i++] = "/bin/bash";
        commands[i++] = pathOfBashScript(processType);

        for (String arg : args){ commands[i++] = arg; }

        return new ProcessBuilder(commands);
    }

    private static String pathOfBashScript(VideoProcessType processType){
        if(processType == VideoProcessType.extract_audio)
            return BASH_SCRIPTS_PATH + AUDIO_EXTRACTOR_BASH_FILENAME;
        if(processType == VideoProcessType.convert)
            return BASH_SCRIPTS_PATH + MEDIA_CONVERTER_BASH_FILENAME;
        return null;
    }

    @SneakyThrows(IOException.class)
    public static void runProcess(ProcessBuilder processBuilder){
        Process process = processBuilder.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = br.readLine()) != null) { System.out.println(line); }
    }
}
