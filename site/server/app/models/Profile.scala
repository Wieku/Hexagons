package models

private object Profile {
  private val qPlayerRankedScore = SqlUtil.getQuery("rank/playerRankedScore")
  private val qPlayerGlobalRank = SqlUtil.getQuery("rank/playerGlobalRank")
  private val qPlayerOverallScore = SqlUtil.getQuery("rank/playerOverallScore")
  private val qPlayerPlayCount = SqlUtil.getQuery("rank/playerPlayCount")
}

case class Profile(id: Int, name: String) {
  private var initiated = false
  private var _rankedScore = -1
  private var _globalRank = -1
  private var _playCount = -1
  private var _totalScore = -1

  private def init(): Unit = {
    if(initiated) return
    initiated = true

    _globalRank = SqlUtil.getIntForQueryI(Profile.qPlayerGlobalRank, id)
    _playCount = SqlUtil.getIntForQueryI(Profile.qPlayerPlayCount, id)
    _rankedScore = SqlUtil.getIntForQueryI(Profile.qPlayerRankedScore, id)
    _totalScore = SqlUtil.getIntForQueryI(Profile.qPlayerOverallScore, id)
  }

  def globalRank: Int = {
    init()
    _globalRank
  }

  def playCount: Int = {
    init()
    _playCount
  }

  def rankedScoreK: Int = {
    init()
    _rankedScore / 1000
  }

  def totalScoreK: Int = {
    init()
    _totalScore / 1000
  }
}
