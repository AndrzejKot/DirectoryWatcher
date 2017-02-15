package com.gft.controller;

import com.gft.iterable.IterableNode;
import com.gft.node.DirNode;
import lombok.extern.log4j.Log4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

@Log4j
@RestController
final class RequestController {

    @Value("${root.dir}")
    private String root;

    @RequestMapping(value = "/addFile", method = RequestMethod.GET)
    String addFile(@RequestParam(value = "name") String name) throws IOException {
        val path = Paths.get(File.separator + root + File.separator + name);
        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            log.debug(e);
            return "File: " + name + " already exists! Try with a different name.";
        }
        return "File: " + name + " created successfully.";
    }

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    List<String> startWatching(@RequestParam(value = "root") String dir, HttpSession httpSession) {
        httpSession.setMaxInactiveInterval(30);
        return getInitialDirStructure();
    }

    private LinkedList<String> getInitialDirStructure() {
        val paths = new LinkedList<String>();
        for (Path element : new IterableNode<Path>(new DirNode(Paths.get(File.separator + root)))) {
            paths.add(element.toString());
        }
        return paths;
    }
}
