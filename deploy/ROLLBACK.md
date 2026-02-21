# 回滚方案

## GitHub 版本回退

- 回退到指定提交：`git revert <commit>` 并推送到 main
- 回退到指定标签：`git checkout <tag>`，建立新分支并合并回 main

## Railway 回滚

- Deployments 中选择历史版本并执行 Redeploy
- 若使用环境变量变更导致故障，优先回滚变量并重启服务

## Vercel 回滚

- Deployments 中选择上一个正常版本并 Promote to Production
- 若仅前端环境变量错误，修正变量后重新部署

## 数据库回滚

- Supabase 建议启用 Point-in-Time Recovery 或定期备份
- 数据迁移脚本保持幂等，新增字段优先可向后兼容
