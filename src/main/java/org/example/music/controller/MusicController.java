package org.example.music.controller;


import org.example.music.common.Result;
import org.example.music.entity.Music;
import org.example.music.service.MusicService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/music")
public class MusicController {
    
    private final MusicService musicService;

    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    //1.上传音乐接口，post请求
    @PostMapping("/upload")
    public Result<Music> uploadMusic(
            @RequestPart("file")MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("artist") String artist,
            @RequestParam(value = "tags",required = false) String tags
            ){
        try{
            Music music = musicService.uploadMusic(file,title,artist,tags);
            return Result.success(music);
        } catch (IllegalArgumentException e) {
            return Result.fail(400, e.getMessage());
        } catch (RuntimeException e) {
            return Result.fail(500, e.getMessage());
        }

    }

    @GetMapping("/list")
    public Result<List<Music>> listAllMusic(){
       try{
           List<Music> musicList = musicService.listAllMusic();
           return Result.success(musicList); // 替换成硬编码列表
       } catch (Exception e) {
           return Result.fail(500, e.getMessage());
       }
    }

    @GetMapping("/play/{id}")
    // 注意：该接口返回音乐文件流，不是JSON格式，因此不返回Result对象
    public ResponseEntity<Resource> playMusic(@PathVariable("id") Long id){
        try{
            Music music=musicService.getMusicById(id);
            if(music==null){
                return ResponseEntity.notFound().build();
            }
            File musicFile =new File(music.getFilePath());
            if(!musicFile.exists()){
                return ResponseEntity.notFound().build();
            }
            Resource resource = new FileSystemResource(musicFile);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/mp4"));
            headers.setContentDisposition(ContentDisposition.inline().filename(musicFile.getName()).build());
            return ResponseEntity.ok().headers(headers).body(resource);
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping("/switch")
    public Result<Music> switchMusic(@RequestParam("currentId") Long currentId,@RequestParam("type") String type){
        try{
            Music music=musicService.switchMusic(currentId,type);
            return Result.success(music);
        }catch (Exception e){
            return Result.fail(500, e.getMessage());
        }
    }
}
