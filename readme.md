## maven仓库发布

### 发布SNAPSHOT版本

```bash
mvn clean deploy
```

### 发布Staging版本

```bash
mvn release:clean
mvn release:prepare
mvn release:perform
```

>查看tag命名：【git tag looks like for my repository】
