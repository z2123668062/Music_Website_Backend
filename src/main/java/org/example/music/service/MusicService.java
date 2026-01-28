package org.example.music.service;

import org.example.music.entity.Music;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MusicService {
    //对应上层的controller层，要实现上传音乐、获取列表、播放音乐、切换音乐等功能

    /*
     *上传音乐
     *音乐文件
     * 歌曲名
     * 歌手名
     * 标签
     * 返回上传成功的音乐对象
     */
    Music uploadMusic(MultipartFile file,String title, String artist, String tags);

    /*
     * 获取列表
     * 查询一个音乐列表
     */
    List<Music> listAllMusic();

    /*
     * 播放音乐
     * 根据要放的音乐id查单首音乐
     * 返回一个音乐对象
     */
    Music getMusicById(Long id);

    /*
     * 切换音乐
     * 当前的音乐id
     * 切换音乐的类型
     * 返回切歌后的音乐对象
     */
    Music switchMusic(Long currentId,String type);
}
