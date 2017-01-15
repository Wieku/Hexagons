package models

import play.api.Play.current

private object Profile {
  private val qPlayerRankedScore = SqlUtil.getQuery("rank/playerRankedScore")
  private val qPlayerGlobalRank = SqlUtil.getQuery("rank/playerGlobalRank")
  private val qPlayerOverallScore = SqlUtil.getQuery("rank/playerOverallScore")
  private val qPlayerPlayCount = SqlUtil.getQuery("rank/playerPlayCount")
  private val qMostFrequentMaps = SqlUtil.getQuery("rank/mostFrequentMaps")
  private val qGetUserName = SqlUtil.getQuery("getUserName")
}

case class Profile(id: Int, ownName: String = null, nFrequentMaps: Int = 12) {
  private var initiated = false
  private var _rankedScore = -1
  private var _globalRank = -1
  private var _playCount = -1
  private var _totalScore = -1
  private var _name: String = _
  private var _freqMaps: Array[MapFreqEntry] = _

  private def init(): Unit = {
    if(initiated) return
    initiated = true

    _globalRank = SqlUtil.getIntForQueryI(Profile.qPlayerGlobalRank, id)
    _playCount = SqlUtil.getIntForQueryI(Profile.qPlayerPlayCount, id)
    _rankedScore = SqlUtil.getIntForQueryI(Profile.qPlayerRankedScore, id)
    _totalScore = SqlUtil.getIntForQueryI(Profile.qPlayerOverallScore, id)
    _name = SqlUtil.getStringForQueryI(Profile.qGetUserName, id)
    play.api.db.DB.withConnection(conn => {
      val statement = conn.prepareStatement(Profile.qMostFrequentMaps)
      statement.setInt(1, id)
      statement.setInt(2, nFrequentMaps)
      val rs = statement.executeQuery()
      _freqMaps = new Array(nFrequentMaps)
      var lastRow = 0
      while(rs.next()) {
        _freqMaps(rs.getRow - 1) = MapFreqEntry(rs.getString(1), rs.getInt(2))
        lastRow = rs.getRow - 1
      }
      if(rs.getRow < nFrequentMaps) {
        _freqMaps = _freqMaps.slice(0, lastRow)
      }
    })
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

  def name: String = {
    init()
    _name
  }

  def exists: Boolean = {
    init()
    _name != null
  }

  def freqMaps: Array[MapFreqEntry] = {
    init()
    _freqMaps
  }

  case class MapFreqEntry(name: String, playCount: Int)
}
