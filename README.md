# PolyFun Android APP

- 为你的图片生成Low Poly风格图片的app
- 通过一张输入图像，输出其对应的 low-poly 图

##Demo
<img src="showUI/1.jpg" height="400"/>
<img src="showUI/2.jpg" height="400"/>
<img src="showUI/3.jpg" height="400"/>
- 直接下载：[http://fir.im/polyfun](http://fir.im/polyfun)

##图片结果
<img src="showUI/sample01.jpg" height="600"/>
<img src="showUI/sample_res01.png" height="600"/><br/>
<img src="showUI/sample02.jpg" height="600"/>
<img src="showUI/sample_res02.png" height="600"/>

## 算法
- 通过灰度转化图片取边缘点以及随机取点
- Delaunay三角剖分算法: 将生成的点组成三角形
- 取三角形中点颜色为每个三角形涂色
- 完成

## 开源协议

[GPL v3](LICENSE)
