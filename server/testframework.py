import unittest
import backend

class TestNeutralLineReceived(unittest.TestCase):
  def setUp(self):
    self.ggX = backend.gogodeXProtocol

  def test_thiswillpass(self):
    self.assertTrue(True)

  def test_thiswillfail(self):
    self.assertTrue(False)

if __name__ == '__main__':
  unittest.main()
