package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.stream.Collectors;

//Controller 어노테이션을 클래스에 붙이면 나중에 해당 마커 어노테이션을 붙은 클래스를 찾아서 내부 메서드를 뒤질 수 있음
@Controller
//@RequestMapping("/api")
public class MyController {
    //RequestMapping이 붙어 있으므로 핸들러 메서드로 인식되어 DisparcherSevlet의 요청 처리 위임이 진행됨
    //hello 주소로 요청이 오고 메서드가 GET이라면 반응하도록 설정
//    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @GetMapping("/hello")
    //요청 메시지, 응답 메시지 파라미터로 전달받기
    public void hello(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //200 ok 상태 코드 설정(문법적으로 오류가 없고 정상적으로 서버에서 응답을 줌)
        response.setStatus(200);
        //헤더 설정
        response.setHeader("Context-Type", "text/plain; chartset=utf-8");
        //바디에 포함될 텍스트 데이터 작성
        response.getWriter().write("hello");
        //localhost:1234/hello 접속 시 hello 보임
    }

    @GetMapping("/echo")
    public void echo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 메서드 정보 접근
        String method = request.getMethod();
        System.out.println("Method : " + method);

        // 주소 정보 접근
        String uri = request.getRequestURI();
        System.out.println("URI : " + uri);

        // 쿼리 스트링 정보 접근
        String query = request.getQueryString();
        System.out.println("Query String : " + query);

        // 프로토콜, HTTP 버전 정보 접근
        String protocol = request.getProtocol();
        System.out.println("Protocol : " + protocol);

        // 헤더 정보 접근
        System.out.println("Headers");
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames != null && headerNames.hasMoreElements()) {
            String h = headerNames.nextElement();
            System.out.println(h + " : " + request.getHeader(h));
        }

        // 요청 메시지의 바디 데이터 접근
        // 1. 바디 데이터를 추가하기 위해서는 POSTMAN과 같은 클라이언트 프로그램을 사용해야 함
        // 2. 일반적으로, GET 요청은 바디 데이터를 추가하지 않는 것이 권장되며, 특정 데이터를 보내기 위해서 쿼리스트링을 이용함
        byte[] bytes = request.getInputStream().readAllBytes();
        String bytesToString = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(bytesToString);

        // 응답 헤더 설정
        response.setHeader("Content-Type", "text/plain; charset=utf-8");
        // 전달받은 body 텍스트를 그대로 응답하도록 설정
        response.getWriter().write(bytesToString);
    }

    @GetMapping("/hello-html")
    public void helloHTML(HttpServletResponse response) throws IOException {
        response.setStatus(200);
        // 만약 해당 내용을 text/plain으로 바꾸면?
        response.setHeader("Content-Type", "text/html; charset=utf-8");
        response.getWriter().write("<h1>Hello</h1>");
    }

    @GetMapping("/hello-xml")
    public void helloXML(HttpServletResponse response) throws IOException {
        // 상태 코드와 관련된 상수를 제공하므로 이용해도 무방함
        response.setStatus(HttpStatus.OK.value());
        // "text/xml"이 아님을 유의
        response.setHeader("Content-Type", "application/xml; charset=utf-8");
        response.getWriter().write("<text>Hello</text>");
    }

    @GetMapping("/hello-json")
    public void helloJSON(HttpServletResponse response) throws IOException {
        // 성공적으로 리소스를 찾아서 돌려주면서 404 코드를 돌려줘도, 스프링 쪽에서는 속사정을 알 방법이 없으니 허용하고 잘 동작함
        // (리소스가 존재하지 않는 이유를 json 같은걸로 설명할 수도 있으므로, HTTP 스펙 상에서도 204와는 달리, 404 코드를 돌려줄 때
        // 바디 데이터를 포함하지 않아야 된다고 명시하지 않았음. 단, 웹 브라우저에서는 404이므로 문제라고 인식함, 그렇다고 해도 4XX
        // 에러에 대한 처리는 프로그래머가 해야 함)
        response.setStatus(404);
        // "text/json"이 아님을 유의
        response.setHeader("Content-Type", "application/json");
        response.getWriter().write("{ \"data\": \"Hello\" }");
    }

    @GetMapping("/echo-repeat")
    public void echoRepeat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setHeader("Content-Type", "text/plain");
        // X-Repeat-Count에 적힌 숫자 정보 가져오고 없으면 1로 초기화
        int loopCount = Integer.parseInt(request.getHeader("X-Repeat-Count") == null ? "1" : request.getHeader("X-Repeat-Count"));
        // 쿼리 스트링 정보 가져와서
        String query = request.getQueryString();
        // 쿼리 스트링 나누고
        String[] querySplit = query.split("&");
        String result = "";

        // 각 쿼리 스트링 정보들을 X-Repeat-Count만큼 반복해서 보여주기
        for(String s : querySplit) {
            for(int i=0;i<loopCount;i++) {
                String[] tmp = s.split("=");
                result += tmp[0] + "," + tmp[1] + "\n";
            }
        }

        response.getWriter().write(result.trim());
    }

    @GetMapping("/dog-image")
    public void dogImage(HttpServletResponse response) throws IOException {
        // resources 폴더의 static 폴더에 이미지 있어야 함
        File file = ResourceUtils.getFile("classpath:static/dog.jpg");
        // 파일의 바이트 데이터 모두 읽어오기
        byte[] bytes = Files.readAllBytes(file.toPath());

        response.setStatus(200);
        // 응답 메시지의 데이터가 JPEG 압축 이미지임을 설정
        response.setHeader("Content-Type", "image/jpeg");
        // 바이트 데이터 쓰기 (여기서는 텍스트 데이터를 전송하지 않기 떄문에 Writer 대신 OutputStream을 이용)
        response.getOutputStream().write(bytes);
    }

    @GetMapping("/dog-image-file")
    public void dogImageFile(HttpServletResponse response) throws IOException {
        File file = ResourceUtils.getFile("classpath:static/dog.jpg");
        byte[] bytes = Files.readAllBytes(file.toPath());

        response.setStatus(200);
        // 임의의 바이너리 데이터임을 알려주는 MIME 타입 설정
        response.setHeader("Content-Type", "application/octet-stream");
        // Content-Length는 자동으로 파일 크기만큼 설정해주지만 여기서는 그냥 넣었음
        // (Q) 만약 바이트 크기를 제대로 주지 않으면 어떻게 될까?)
        response.setHeader("Content-Length", bytes.length + "");
        // 다운로드 될 파일 이름 설정
        String filename = "dog.jpg";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.getOutputStream().write(bytes);
    }

    private ArrayList<String> wordList = new ArrayList<>();

    // 위의 ArrayList에 단어를 추가하는 메서드
    @PostMapping("/words")
    public void addWord(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String bodyString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String[] words = bodyString.split("\n");
        for(String w : words) wordList.add(w.trim());

        // 201 CREATED 응답 코드 발생시키기
        response.setStatus(201);
        // 생성된 리소스를 확인할 수 있는 URL 알려주기 (Location 헤더 굳이 안 붙여도 기능적으로 차이는 없음)
        response.setHeader("Location", "/words");
    }

    // 저장된 모든 단어 보여주기
    @GetMapping("/words")
    public void showWords(HttpServletResponse response) throws IOException {
        String allWords = String.join(",", wordList);

        response.setStatus(200);
        response.getWriter().write(allWords);
    }

    @GetMapping("/users/{username}/products/{productId}")
    public void getProducts(
            @PathVariable(value = "username", required = true) String username,
            @PathVariable("productId") Integer productId,
            @RequestParam(value = "show_comments", required = false, defaultValue = "true") Boolean showComments,
            @RequestHeader("API-Token") String apiToken,
            HttpServletResponse response) throws IOException {

        System.out.println(username);
        System.out.println(productId);
        System.out.println(showComments);
        System.out.println(apiToken);

        if(!apiToken.equals("secret")) {
            response.setStatus(401);
            response.getWriter().write("need valid api key");
        } else {
            response.setStatus(200);
            response.getWriter().write("success");
        }
    }
}
