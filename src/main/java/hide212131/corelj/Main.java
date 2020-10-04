package hide212131.corelj;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import hide212131.corelj.json.ClassEntry;
import hide212131.corelj.json.MethodEntry;
import hide212131.corelj.visitor.MyClassVisitor;
import hide212131.corelj.visitor.SignatureFilter;
import org.apache.commons.cli.*;
import org.objectweb.asm.ClassReader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) throws IOException {

        Options options = new Options();
        options.addOption(Option.builder("help").desc("print this message").build());
        options.addOption(Option.builder("includeClass")
                .argName("PATTERN")
                .hasArg()
                .desc("don't exclude classes matching PATTERN")
                .build());

        options.addOption(Option.builder("excludeClass")
                .argName("PATTERN")
                .hasArg()
                .desc("exclude classes matching PATTERN")
                .build());

        options.addOption(Option.builder("includeMethod")
                .argName("PATTERN")
                .hasArg()
                .desc("don't exclude methods matching PATTERN")
                .build());

        options.addOption(Option.builder("excludeMethod")
                .argName("PATTERN")
                .hasArg()
                .desc("exclude methods matching PATTERN")
                .build());

        options.addOption(Option.builder("includeMethodInsn")
                .argName("PATTERN")
                .hasArg()
                .desc("don't exclude methods instruction matching PATTERN")
                .build());

        options.addOption(Option.builder("excludeMethodInsn")
                .argName("PATTERN")
                .hasArg()
                .desc("exclude methods instruction matching PATTERN")
                .build());

        options.addOption(Option.builder("include")
                .argName("PATTERN")
                .hasArg()
                .desc("don't exclude matching PATTERN")
                .build());

        options.addOption(Option.builder("exclude")
                .argName("PATTERN")
                .hasArg()
                .desc("exclude matching PATTERN")
                .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("[opt]", options);
            exit(1);
            return;
        }

        var classFilter = new SignatureFilter()
                .include(cmd.getOptionValue("includeClass"))
                .include(cmd.getOptionValue("include"))
                .exclude(cmd.getOptionValue("excludeClass"))
                .exclude(cmd.getOptionValue("exclude"));

        var methodFilter = new SignatureFilter()
                .include(cmd.getOptionValue("includeMethod"))
                .include(cmd.getOptionValue("include"))
                .exclude(cmd.getOptionValue("excludeMethod"))
                .exclude(cmd.getOptionValue("exclude"));

        var methodInsnFilter = new SignatureFilter()
                .include(cmd.getOptionValue("includeMethodInsn"))
                .include(cmd.getOptionValue("include"))
                .exclude(cmd.getOptionValue("excludeMethodInsn"))
                .exclude(cmd.getOptionValue("exclude"));

        List<ClassEntry> classEntryList = new ArrayList<>();

        for (String jarFileName : cmd.getArgs()) {
            try (FileInputStream fis = new FileInputStream(jarFileName);
                 BufferedInputStream bis = new BufferedInputStream(fis);
                 ZipInputStream zis = new ZipInputStream(bis)) {
                ZipEntry jarEntry;
                while ((jarEntry = zis.getNextEntry()) != null) {
                    if (!jarEntry.isDirectory() && jarEntry.getName().endsWith(".class")) {
                        String s = jarEntry.getName();
                        String className = s.substring(0, s.lastIndexOf("."))
                                .replace("/", ".");
                        if (classFilter.match(className)) {
                            ClassReader classReader = new ClassReader(zis);
                            var cv = new MyClassVisitor(methodFilter, methodInsnFilter);
                            classReader.accept(cv, 0);
                            classEntryList.add(cv.getClassEntry());
                        }
                    }
                }
            }
        }

        var objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        List<MethodEntry> list = classEntryList.stream()
                .flatMap(c -> c.methodEntryList.stream())
                .collect(Collectors.toList());
        String classJson = objectMapper.writeValueAsString(list);
        System.out.println(classJson);

    }
}
