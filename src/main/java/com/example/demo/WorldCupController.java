package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WorldCupController {

    private List<String> images = new ArrayList<>();
    private List<String> roundImages = new ArrayList<>();
    private int round = 1; // 현재 진행 중인 라운드 번호

    // 생성자에서 이미지 파일 경로 추가
    public WorldCupController() {
        // 32개의 이미지 파일 경로를 동적으로 추가 (64강을 32강으로 변경)
        for (int i = 1; i <= 32; i++) {
            images.add("image" + i + ".jpg");
        }
        Collections.shuffle(images); // 초기 이미지를 섞습니다.
        roundImages.addAll(images); // 첫 라운드 이미지를 roundImages에 추가
    }

    @GetMapping("/worldcup")
    public String worldcup(Model model) {
        // 각 라운드마다 2개의 이미지를 선택
        if (roundImages.size() > 1) {
            model.addAttribute("image1", "/images/" + roundImages.get(0));
            model.addAttribute("image2", "/images/" + roundImages.get(1));
        }

        model.addAttribute("round", round); // 현재 라운드 번호
        
        // 라운드 알림 추가
        String roundAnnouncement = "";
        if (round == 17) {
            roundAnnouncement = "16강 시작!";
        } else if (round == 25) {
            roundAnnouncement = "8강 시작!";
        } else if (round == 29) {
            roundAnnouncement = "4강 시작!";
        } else if (round == 31) {
            roundAnnouncement = "준결승 시작!";
        } else if (round == 32) {
            roundAnnouncement = "결승 시작!";
        }

        model.addAttribute("roundAnnouncement", roundAnnouncement); // 라운드 알림 추가
        return "worldcup";
    }

    @PostMapping("/select")
    public String selectWinner(@RequestParam String winner) {
        // 승자를 다음 라운드로 보내기
        if (winner.equals("image1")) {
            roundImages.add(roundImages.get(0)); // 첫 번째 이미지를 승자로 추가
        } else {
            roundImages.add(roundImages.get(1)); // 두 번째 이미지를 승자로 추가
        }

        // 두 이미지를 제외한 나머지 이미지들로 다음 라운드를 진행
        roundImages.remove(0);
        roundImages.remove(0);

        // 라운드가 끝나면 round을 증가시켜 다음 라운드로 진행
        round++;

        // 라운드가 끝나면 결승전을 진행
        if (roundImages.size() == 1) {
            return "redirect:/winner";
        }

        return "redirect:/worldcup"; // 다음 라운드 진행
    }

    @GetMapping("/winner")
    public String winner(Model model) {
        model.addAttribute("winner", roundImages.get(0)); // 최종 승자 이미지
        return "winner";
    }
}
