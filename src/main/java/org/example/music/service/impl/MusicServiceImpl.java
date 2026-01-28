package org.example.music.service.impl;

import org.example.music.entity.Music;
import org.example.music.mapper.MusicMapper;
import org.example.music.service.MusicService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("DuplicateBranchesInSwitch")
@Service
public class MusicServiceImpl implements MusicService {
    // 依赖注入Mapper层（操作数据库）
    private final MusicMapper musicMapper;

    @Value("${music.upload.path}")
    private String musicUploadRootPath;

    // 构造器注入（推荐，比@Autowired更规范）
    public MusicServiceImpl(MusicMapper musicMapper) {
        this.musicMapper = musicMapper;
    }

    //1.上传音乐的逻辑
    @Override
    public Music uploadMusic (MultipartFile file, String title, String artist, String tags){
        //1.校验文件/文件名是否为空
        if(file.isEmpty()||title == null){
            throw new IllegalArgumentException("文件/文件名为空");
        }
        //2.校验文件格式
        String originalFileName = file.getOriginalFilename();
        if(originalFileName==null||!originalFileName.endsWith(".m4a")){
            throw new IllegalArgumentException("文件类型错误，需要m4a");
        }
        //3.生成唯一文件名（避免重复）,将原文件名.mp3变成原文件名_时间戳.mp3
        String fileNamePrefix = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        String uniqueFileName = fileNamePrefix + "_" + System.currentTimeMillis() + ".m4a";

        //4.拼接完整存储路径（根路径+唯一文件名）
        String fullFilePath = musicUploadRootPath + uniqueFileName;

        //5.创建存储目录（如果不存在）
        File saveFile = new File(fullFilePath);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();  // 递归创建目录
        }

        //6.保存文件到本地
        try{
            file.transferTo(saveFile);
        } catch (IOException e) {
            throw new RuntimeException("保存文件时出错"+e.getMessage());
        }

        //7.封装music对象，存入数据库
        Music music = new Music();
        music.setTitle(title);
        music.setArtist(artist);
        music.setTags(tags);
        music.setFilePath(fullFilePath);
        music.setUploadTime(LocalDateTime.now());

        //8.调用mapper插入数据库
        musicMapper.insert(music);
        return music;

    }

    //2.获取列表，用MyBatis-Plus自带的selectList查全表，返回的list列表里面是一个个music对象。
    // 不过当数据增加的时候不建议这样查，效率不高，后期可以修改。
    @Override
    public List<Music> listAllMusic() {
        List<Music> list = musicMapper.selectList(null);
        return list == null ? new ArrayList<>() : list;
    }

    //3.播放音乐，根据ID查单个音乐
    @Override
    public Music getMusicById(Long id){
        return musicMapper.selectById(id);
    }

    //4.切换音乐，根据当前ID和切换类型切歌
    @Override
    public Music switchMusic(Long currentId,String type){
        //先获取所有音乐的列表
        List<Music> allmusicList = listAllMusic();

        //判断一下列表是不是空的
        if(allmusicList.isEmpty()){
            throw new RuntimeException("暂无音乐播放");
        }

        //获取所有音乐的ID，放到数组中
        Long[] musicIds = allmusicList.stream().map(Music::getId).toArray(Long[]::new);
        int currentIndex = -1;//初始索引

        for(int i=0;i<musicIds.length;i++){
            if(musicIds[i].equals(currentId)){
                currentIndex = i;
            }
        }
        if(currentIndex == -1){//如果要查的那个音乐ID不存在，就放第一首
            return allmusicList.get(0);
        }

        //根据切歌的逻辑计算下一首歌索引
        int nextIndex = switch (type) {
            case "prev" -> currentIndex - 1 > 0 ? currentIndex - 1 : 0;
            case "next" -> currentIndex + 1 >= musicIds.length ? 0 : currentIndex + 1;
            case "random" -> new Random().nextInt(musicIds.length);
            default -> currentIndex + 1 >= musicIds.length ? 0 : currentIndex + 1;
        };

        //返回要放的下一首歌
        return allmusicList.get(nextIndex);
    }


}
