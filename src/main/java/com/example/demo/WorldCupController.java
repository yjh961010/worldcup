package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@SessionAttributes({"round", "roundImages"})
public class WorldCupController {

    private List<String> images = new ArrayList<>();
    private int round = 1; // 현재 진행 중인 라운드 번호

    // 생성자에서 이미지 파일 경로 추가
    public WorldCupController() {
        // 64개의 이미지 파일 경로를 동적으로 추가
        for (int i = 1; i <= 64; i++) {
            images.add("image" + i + ".jpg");
        }
        Collections.shuffle(images); // 초기 이미지를 섞습니다.
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("worldcups", List.of("월드컵1", "월드컵2", "월드컵3"));
        return "index";
    }

    @GetMapping("/worldcup")
    public String worldcup(Model model, HttpSession session) {
        // 세션에서 라운드 번호와 이미지 목록을 불러옵니다.
        List<String> roundImages = (List<String>) session.getAttribute("roundImages");
        Integer round = (Integer) session.getAttribute("round");

        // 세션에 저장된 라운드 번호와 이미지 목록이 없다면 초기화
        if (roundImages == null || round == null) {
            round = 1;
            roundImages = new ArrayList<>(images);
            session.setAttribute("round", round); // 세션에 라운드 번호 저장
            session.setAttribute("roundImages", roundImages); // 세션에 이미지 목록 저장
        }

        // 각 라운드마다 2개의 이미지를 선택
        if (roundImages.size() > 1) {
            model.addAttribute("image1", "/images/" + roundImages.get(0));
            model.addAttribute("image2", "/images/" + roundImages.get(1));
        }

        model.addAttribute("round", round); // 현재 라운드 번호
        
        // 라운드 알림 추가
        String roundAnnouncement = getRoundAnnouncement(roundImages.size());
        model.addAttribute("roundAnnouncement", roundAnnouncement); // 라운드 알림 추가
        return "worldcup";
    }

    @PostMapping("/select")
    public String selectWinner(@RequestParam String winner, HttpSession session) {
        // 세션에서 라운드 이미지 목록을 불러옵니다.
        List<String> roundImages = (List<String>) session.getAttribute("roundImages");

        // 승자를 다음 라운드로 보내기
        if (winner.equals("image1")) {
            roundImages.add(roundImages.get(0)); // 첫 번째 이미지를 승자로 추가
        } else {
            roundImages.add(roundImages.get(1)); // 두 번째 이미지를 승자로 추가
        }

        // 두 이미지를 제외한 나머지 이미지들로 다음 라운드를 진행
        roundImages.remove(0);
        roundImages.remove(0);

        // 라운드 번호 증가
        int round = (int) session.getAttribute("round");
        round++;

        session.setAttribute("round", round); // 라운드 번호 업데이트
        session.setAttribute("roundImages", roundImages); // 이미지 목록 업데이트

        // 라운드가 끝나면 결승전 페이지로 리디렉션
        if (roundImages.size() == 1) {
            return "redirect:/winner";
        }

        return "redirect:/worldcup"; // 다음 라운드 진행
    }

    @GetMapping("/winner")
    public String winner(Model model, HttpSession session) {
        List<String> roundImages = (List<String>) session.getAttribute("roundImages");
        model.addAttribute("winner", roundImages.get(0)); // 최종 승자 이미지
        return "winner";
    }

    // 라운드 알림 메시지
    private String getRoundAnnouncement(int remainingImages) {
        switch (remainingImages) {
            case 64:
                return "64강 진행 중!";
            case 32:
                return "32강 진행 중!";
            case 16:
                return "16강 진행 중!";
            case 8:
                return "8강 진행 중!";
            case 4:
                return "4강 진행 중!";
            case 2:
                return "준결승 진행 중!";
            case 1:
                return "결승 진행 중!";
            default:
                return "";
        }
    }
}
