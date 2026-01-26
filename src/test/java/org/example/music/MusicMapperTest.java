package org.example.music;

import org.example.music.entity.Music;
import org.example.music.mapper.MusicMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest  // 启动Spring容器进行测试
public class MusicMapperTest {

    @Autowired
    private MusicMapper musicMapper;

    // 测试新增音乐
    @Test
    public void testInsert() {
        Music music = new Music();
        music.setTitle("七里香");
        music.setArtist("周杰伦");
        music.setTags("流行,华语");
        music.setFilePath("src/main/resources/static/upload/music/七里香.mp3");
        music.setUploadTime(LocalDateTime.now());

        int rows = musicMapper.insert(music);
        System.out.println("新增成功，影响行数：" + rows);
        System.out.println("新增的音乐ID：" + music.getId());  // 自增ID会自动回填
    }

    // 测试查询所有音乐
    @Test
    public void testSelectList() {
        List<Music> musicList = musicMapper.selectList(null);  // null表示无查询条件
        musicList.forEach(System.out::println);
    }
}