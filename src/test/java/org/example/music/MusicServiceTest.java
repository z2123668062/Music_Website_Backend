package org.example.music;

import org.example.music.entity.Music;
import org.example.music.service.MusicService;
import org.example.music.service.impl.MusicServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@SpringBootTest
public class MusicServiceTest {

    @Autowired
    private MusicService musicService;

    // 测试上传音乐（用MockMultipartFile模拟文件上传）
    @Test
    public void testUploadMusic() {
        // 模拟一个m4a文件
        MultipartFile mockFile;
        try {
            mockFile = new MockMultipartFile(
                    "file",
                    "test.m4a",
                    "audio/mp4",
                    new FileInputStream("E:/music_web/music/src/test/resource/春泥.m4a")  // 本地测试m4a文件路径
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 调用上传方法
        Music music = musicService.uploadMusic(mockFile, "测试歌曲", "测试歌手", "测试标签");
        System.out.println("上传成功，音乐ID：" + music.getId());
    }

    // 测试查询所有音乐
    @Test
    public void testListAllMusic() {
        List<Music> musicList = musicService.listAllMusic();
        System.out.println("音乐列表：" + musicList);
    }

    // 测试切歌（替换currentId为你数据库中存在的ID）
    @Test
    public void testSwitchMusic() {
        Music nextMusic = musicService.switchMusic(1L, "next");  // 当前ID=1，切下一首
        System.out.println("切歌后的音乐：" + nextMusic.getTitle());
    }
}